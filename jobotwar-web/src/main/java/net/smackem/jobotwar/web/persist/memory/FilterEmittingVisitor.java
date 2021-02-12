package net.smackem.jobotwar.web.persist.memory;

import com.google.common.base.CharMatcher;
import net.smackem.jobotwar.web.query.PQueryBaseVisitor;
import net.smackem.jobotwar.web.query.PQueryParser;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.function.IntFunction;

class FilterEmittingVisitor extends PQueryBaseVisitor<Filter> {
    @Override
    public Filter visitQuery(PQueryParser.QueryContext ctx) {
        return super.visitQuery(ctx);
    }

    @Override
    public Filter visitOrCondition(PQueryParser.OrConditionContext ctx) {
        Filter filter = ctx.andCondition(0).accept(this);
        int index = 1;
        for (final var ignored : ctx.Or()) {
            final Filter right = ctx.andCondition(index).accept(this);
            filter = new BinaryFilter(filter, right, FilterEmittingVisitor::or);
        }
        return filter;
    }

    @Override
    public Filter visitAndCondition(PQueryParser.AndConditionContext ctx) {
        Filter filter = ctx.condition(0).accept(this);
        int index = 1;
        for (final var ignored : ctx.And()) {
            final Filter right = ctx.condition(index).accept(this);
            filter = new BinaryFilter(filter, right, FilterEmittingVisitor::and);
        }
        return filter;
    }

    @Override
    public Filter visitCondition(PQueryParser.ConditionContext ctx) {
        Filter filter = ctx.comparison().accept(this);
        if (ctx.Not() != null) {
            filter = new UnaryFilter(filter, FilterEmittingVisitor::not);
        }
        return filter;
    }

    @Override
    public Filter visitComparison(PQueryParser.ComparisonContext ctx) {
        if (ctx.orCondition() != null) {
            return ctx.orCondition().accept(this);
        }
        final PQueryParser.ComparatorContext comparator = ctx.comparator();
        final Function<Object, Object> left = internalVisitAtom(ctx.atom(0));
        final Function<Object, Object> right = internalVisitAtom(ctx.atom(1));
        final Filter filter;
        if (comparator.Eq() != null) {
            filter = new RelationalFilter(left, right, n -> n == 0);
        } else if (comparator.Ne() != null) {
            filter = new RelationalFilter(left, right, n -> n != 0);
        } else if (comparator.Gt() != null) {
            filter = new RelationalFilter(left, right, n -> n > 0);
        } else if (comparator.Ge() != null) {
            filter = new RelationalFilter(left, right, n -> n >= 0);
        } else if (comparator.Lt() != null) {
            filter = new RelationalFilter(left, right, n -> n < 0);
        } else if (comparator.Le() != null) {
            filter = new RelationalFilter(left, right, n -> n <= 0);
        } else if (comparator.Match() != null) {
            filter = new StringMatchFilter(left, right);
        } else {
            throw new IllegalStateException("unsupported operator");
        }
        return filter;
    }

    private Function<Object, Object> internalVisitAtom(PQueryParser.AtomContext ctx) {
        if (ctx.Ident() != null) {
            return propertyValueExtractor(ctx.Ident().getText());
        }
        if (ctx.number() != null) {
            if (ctx.number().Integer() != null) {
                final int n = Integer.parseInt(ctx.number().Integer().getText());
                return ignored -> n;
            }
            if (ctx.number().Real() != null) {
                final double d = Double.parseDouble(ctx.number().Real().getText());
                return ignored -> d;
            }
        }
        if (ctx.String() != null) {
            final String s = CharMatcher.is('\'').trimFrom(ctx.String().getText());
            return ignored -> s;
        }
        return null;
    }

    private static Function<Object, Object> propertyValueExtractor(String propertyName) {
        return bean -> {
            try {
                final Field field = bean.getClass().getDeclaredField(propertyName);
                field.setAccessible(true);
                return field.get(bean);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static Boolean not(Boolean b) {
        return b == false;
    }

    private static Boolean or(Boolean l, Boolean b) {
        return l || b;
    }

    private static Boolean and(Boolean l, Boolean r) {
        return l && r;
    }
}
