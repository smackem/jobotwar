package net.smackem.jobotwar.web.persist.sql;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import net.smackem.jobotwar.web.query.PQueryBaseVisitor;
import net.smackem.jobotwar.web.query.PQueryParser;

import java.util.Objects;


public class SqlEmittingVisitor extends PQueryBaseVisitor<String> {

    private final String tableName;

    SqlEmittingVisitor(String tableName) {
        this.tableName = Objects.requireNonNull(tableName);
    }

    @Override
    public String visitOrCondition(PQueryParser.OrConditionContext ctx) {
        String filter = ctx.andCondition(0).accept(this);
        int index = 1;
        for (final var ignored : ctx.Or()) {
            final String right = ctx.andCondition(index).accept(this);
            filter = "%s or %s".formatted(filter, right);
        }
        return filter;
    }

    @Override
    public String visitAndCondition(PQueryParser.AndConditionContext ctx) {
        String filter = ctx.condition(0).accept(this);
        int index = 1;
        for (final var ignored : ctx.And()) {
            final String right = ctx.condition(index).accept(this);
            filter = "%s and %s".formatted(filter, right);
        }
        return filter;
    }

    @Override
    public String visitCondition(PQueryParser.ConditionContext ctx) {
        String filter = ctx.comparison().accept(this);
        if (ctx.Not() != null) {
            filter = "not(%s)".formatted(filter);
        }
        return filter;
    }

    @Override
    public String visitComparison(PQueryParser.ComparisonContext ctx) {
        if (ctx.orCondition() != null) {
            return '(' + ctx.orCondition().accept(this) + ')';
        }
        final PQueryParser.ComparatorContext comparator = ctx.comparator();
        final String left = visitAtom(ctx.atom(0));
        final String right = visitAtom(ctx.atom(1));
        final String filter;
        if (comparator.Eq() != null) {
            filter = "%s = %s".formatted(left, right);
        } else if (comparator.Ne() != null) {
            filter = "%s <> %s".formatted(left, right);
        } else if (comparator.Gt() != null) {
            filter = "%s > %s".formatted(left, right);
        } else if (comparator.Ge() != null) {
            filter = "%s >= %s".formatted(left, right);
        } else if (comparator.Lt() != null) {
            filter = "%s < %s".formatted(left, right);
        } else if (comparator.Le() != null) {
            filter = "%s <= %s".formatted(left, right);
        } else if (comparator.Match() != null) {
            filter = "%s like %s".formatted(left, translateWildcards(right));
        } else {
            throw new IllegalStateException("unsupported operator");
        }
        return filter;
    }

    @Override
    public String visitAtom(PQueryParser.AtomContext ctx) {
        if (ctx.Ident() != null) {
            final String attributeName = ctx.Ident().getText();
            final String columnName = sanitizeColumnName(attributeName);
            return this.tableName + '.' + columnName;
        }
        if (ctx.number() != null) {
            if (ctx.number().Integer() != null) {
                return ctx.number().Integer().getText();
            }
            if (ctx.number().Real() != null) {
                return ctx.number().Real().getText();
            }
        }
        if (ctx.String() != null) {
            return ctx.String().getText(); // keep ' delimiters
        }
        return null;
    }

    private static String translateWildcards(String s) {
        return CharMatcher.anyOf("*?").replaceFrom(s, '%');
    }

    private static String sanitizeColumnName(String columnName) {
        columnName = CharMatcher.forPredicate(Character::isLetterOrDigit)
                .or(CharMatcher.is('_'))
                .negate()
                .removeFrom(columnName);
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
    }
}
