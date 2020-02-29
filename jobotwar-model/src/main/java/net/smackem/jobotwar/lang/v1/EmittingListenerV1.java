package net.smackem.jobotwar.lang.v1;

import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.common.Emitter;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class EmittingListenerV1 extends JobotwarV1BaseListener {
    private final Map<String, Integer> locals = new HashMap<>();
    private int labelId = 1;
    private boolean passModifierClause;
    private String lastLoadedSymbol;
    private final Emitter emitter;

    EmittingListenerV1(Emitter emitter) {
        this.emitter = Objects.requireNonNull(emitter);
    }

    @Override
    public void exitDeclaration(JobotwarV1Parser.DeclarationContext ctx) {
        for (final TerminalNode id : ctx.ID()) {
            this.locals.put(id.getText(), this.emitter.instructions().size());
            this.emitter.emit(OpCode.LD_F64);
        }
    }

    @Override
    public void exitLabel(JobotwarV1Parser.LabelContext ctx) {
        this.emitter.emit(OpCode.LABEL, ctx.ID().getText());
    }

    @Override
    public void exitAtom(JobotwarV1Parser.AtomContext ctx) {
        final String symbol;
        if (ctx.ID() != null) {
            final String ident = ctx.ID().getText();
            final Integer addr = this.locals.get(ident);
            if (addr == null) {
                throw new RuntimeException("Unknown local " + ident);
            }
            this.emitter.emit(OpCode.LD_GLB, addr);
            symbol = ident;
        } else if (ctx.number() != null) {
            final String literal = ctx.number().getText();
            this.emitter.emit(OpCode.LD_F64, Double.parseDouble(literal));
            symbol = literal;
        } else if (ctx.register() != null) {
            final String register = ctx.register().getText();
            this.emitter.emit(OpCode.LD_REG, register);
            symbol = register;
        } else {
            symbol = null; // paren expr
        }
        this.lastLoadedSymbol = symbol;
    }

    @Override
    public void exitCondition(JobotwarV1Parser.ConditionContext ctx) {
        if (ctx.conditionOperator() == null) {
            return;
        }
        switch (ctx.conditionOperator().getText()) {
            case "or":
                this.emitter.emit(OpCode.OR);
                break;
            case "and":
                this.emitter.emit(OpCode.AND);
                break;
            default:
                throw new RuntimeException("Unsupported boolean operator " + ctx.conditionOperator().getText());
        }
    }

    @Override
    public void exitComparison(JobotwarV1Parser.ComparisonContext ctx) {
        if (ctx.comparator() == null) {
            return;
        }
        switch (ctx.comparator().getText()) {
            case "=":
                this.emitter.emit(OpCode.EQ);
                break;
            case "!=":
                this.emitter.emit(OpCode.NEQ);
                break;
            case ">":
                this.emitter.emit(OpCode.GT);
                break;
            case ">=":
                this.emitter.emit(OpCode.GE);
                break;
            case "<":
                this.emitter.emit(OpCode.LT);
                break;
            case "<=":
                this.emitter.emit(OpCode.LE);
                break;
            default:
                throw new RuntimeException("Unsupported comparator: " + ctx.comparator().getText());
        }
    }

    @Override
    public void exitTerm(JobotwarV1Parser.TermContext ctx) {
        if (ctx.termOperator() == null) {
            return;
        }
        switch (ctx.termOperator().getText()) {
            case "+":
                this.emitter.emit(OpCode.ADD);
                break;
            case "-":
                this.emitter.emit(OpCode.SUB);
                break;
            default:
                throw new RuntimeException("Unsupported term operator " + ctx.termOperator());
        }
    }

    @Override
    public void exitProduct(JobotwarV1Parser.ProductContext ctx) {
        if (ctx.productOperator() == null) {
            return;
        }
        switch (ctx.productOperator().getText()) {
            case "*":
                this.emitter.emit(OpCode.MUL);
                break;
            case "/":
                this.emitter.emit(OpCode.DIV);
                break;
            case "%":
                this.emitter.emit(OpCode.MOD);
                break;
            default:
                throw new RuntimeException("Unsupported product operator " + ctx.productOperator());
        }
    }

    @Override
    public void exitMolecule(JobotwarV1Parser.MoleculeContext ctx) {
        if (ctx.func() == null) {
            return;
        }
        final String funcName = ctx.func().getText();
        if ("not".equals(funcName)) {
            this.emitter.emit(OpCode.NOT);
        } else {
            this.emitter.emit(OpCode.INVOKE, funcName);
        }
    }

    @Override
    public void enterStatement(JobotwarV1Parser.StatementContext ctx) {
        if (ctx.ifClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.ifClause());
        } else if (ctx.unlessClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.unlessClause());
        }
        this.passModifierClause = true;
    }

    @Override
    public void exitStatement(JobotwarV1Parser.StatementContext ctx) {
        this.emitter.emit(OpCode.LABEL, "@" + this.labelId);
        this.labelId++;
        this.passModifierClause = false;
    }

    @Override
    public void enterIfClause(JobotwarV1Parser.IfClauseContext ctx) {
        this.emitter.setDisabled(this.passModifierClause);
    }

    @Override
    public void enterUnlessClause(JobotwarV1Parser.UnlessClauseContext ctx) {
        this.emitter.setDisabled(this.passModifierClause);
    }

    @Override
    public void exitIfClause(JobotwarV1Parser.IfClauseContext ctx) {
        this.emitter.emit(OpCode.BR_ZERO, "@" + this.labelId);
        this.emitter.setDisabled(false);
    }

    @Override
    public void exitUnlessClause(JobotwarV1Parser.UnlessClauseContext ctx) {
        this.emitter.emit(OpCode.NOT);
        this.emitter.emit(OpCode.BR_ZERO, "@" + this.labelId);
        this.emitter.setDisabled(false);
    }

    @Override
    public void exitGotoStatement(JobotwarV1Parser.GotoStatementContext ctx) {
        this.emitter.emit(OpCode.BR, ctx.ID().getText());
    }

    @Override
    public void exitAssignStatement(JobotwarV1Parser.AssignStatementContext ctx) {
        for (int i = 0; i < ctx.assignTarget().size() - 1; i++) {
            this.emitter.emit(OpCode.DUP);
        }
        for (final JobotwarV1Parser.AssignTargetContext assignTarget : ctx.assignTarget()) {
            if (assignTarget.register() != null) {
                final String ident = assignTarget.register().getText();
                this.emitter.emit(OpCode.ST_REG, ident);
                this.lastLoadedSymbol = ident;
                continue;
            }
            if (assignTarget.ID() != null) {
                final String ident = assignTarget.ID().getText();
                final Integer addr = this.locals.get(ident);
                if (addr == null) {
                    throw new RuntimeException("Unknown local " + assignTarget.ID().getText());
                }
                this.emitter.emit(OpCode.ST_GLB, addr);
                this.lastLoadedSymbol = ident;
                continue;
            }
            if (assignTarget.specialAssignTarget() != null) {
                if (assignTarget.specialAssignTarget().OUT() != null) {
                    this.emitter.emit(OpCode.LOG, this.lastLoadedSymbol);
                    continue;
                }
            }
            throw new RuntimeException("Unsupported assign target");
        }
    }

    @Override
    public void exitGosubStatement(JobotwarV1Parser.GosubStatementContext ctx) {
        this.emitter.emit(OpCode.LD_F64, 0.0);
        this.emitter.emit(OpCode.CALL, ctx.ID().getText());
    }

    @Override
    public void exitEndsubStatement(JobotwarV1Parser.EndsubStatementContext ctx) {
        this.emitter.emit(OpCode.RET);
    }
}
