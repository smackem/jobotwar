package net.smackem.jobotwar.lang;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.*;

public class Emitter extends JobotwarBaseListener {
    private final List<Instruction> instructions = new ArrayList<>();
    private int labelId = 1;
    private boolean disabled;

    public List<Instruction> instructions() {
        return Collections.unmodifiableList(this.instructions);
    }

    @Override
    public void exitLabel(JobotwarParser.LabelContext ctx) {
        emit(OpCode.LABEL, ctx.ID().getText());
    }

    @Override
    public void exitAtom(JobotwarParser.AtomContext ctx) {
        if (ctx.ID() != null) {
            emit(OpCode.PUSH_LOC, ctx.ID().getText());
        } else if (ctx.number() != null) {
            emit(OpCode.PUSH_F64, Double.parseDouble(ctx.number().getText()));
        } else if (ctx.register() != null) {
            emit(OpCode.PUSH_REG, ctx.register().getText());
        }
    }

    @Override
    public void exitComparison(JobotwarParser.ComparisonContext ctx) {
        if (ctx.comparator() == null) {
            return;
        }
        switch (ctx.comparator().getText()) {
            case "=":
            case "!=":
            case ">":
            case ">=":
            case "<":
            case "<=":
            default:
                throw new RuntimeException("Unsupported comparator: " + ctx.comparator().getText());
        }
    }

    @Override
    public void exitTerm(JobotwarParser.TermContext ctx) {
        if (ctx.termOperator() == null) {
            return;
        }
        switch (ctx.termOperator().getText()) {
            case "+":
                emit(OpCode.ADD);
                break;
            case "-":
                emit(OpCode.SUB);
                break;
            default:
                throw new RuntimeException("Unsupported term operator " + ctx.termOperator());
        }
    }

    @Override
    public void exitProduct(JobotwarParser.ProductContext ctx) {
        if (ctx.productOperator() == null) {
            return;
        }
        switch (ctx.productOperator().getText()) {
            case "*":
                emit(OpCode.MUL);
                break;
            case "/":
                emit(OpCode.DIV);
                break;
            case "%":
                emit(OpCode.MOD);
                break;
            default:
                throw new RuntimeException("Unsupported product operator " + ctx.productOperator());
        }
    }

    @Override
    public void enterStatement(JobotwarParser.StatementContext ctx) {
        if (ctx.ifClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.ifClause());
        } else if (ctx.unlessClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.unlessClause());
        }
        this.disabled = true;
    }

    @Override
    public void exitStatement(JobotwarParser.StatementContext ctx) {
        this.disabled = false;
        emit(OpCode.LABEL, this.labelId);
        this.labelId++;
    }

    @Override
    public void exitIfClause(JobotwarParser.IfClauseContext ctx) {
        emit(OpCode.BR_ZERO, this.labelId);
    }

    @Override
    public void exitUnlessClause(JobotwarParser.UnlessClauseContext ctx) {
        emit(OpCode.BR_NONZERO, this.labelId);
    }

    @Override
    public void exitGotoStatement(JobotwarParser.GotoStatementContext ctx) {
        emit(OpCode.BR, ctx.ID().getText());
    }

    @Override
    public void exitAssignTarget(JobotwarParser.AssignTargetContext ctx) {
        if (ctx.register() != null) {
            emit(OpCode.ST_REG, ctx.register().getText());
        } else if (ctx.ID() != null) {
            emit(OpCode.ST_LOC, ctx.ID().getText());
        } else {
            throw new RuntimeException("Unsupported assign target");
        }
    }

    private void emit(OpCode opCode) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(new Instruction(opCode));
    }

    private void emit(OpCode opCode, int intArg) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(new Instruction(opCode, intArg));
    }

    private void emit(OpCode opCode, double f64Arg) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(new Instruction(opCode, f64Arg));
    }

    private void emit(OpCode opCode, String strArg) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(new Instruction(opCode, strArg));
    }
}
