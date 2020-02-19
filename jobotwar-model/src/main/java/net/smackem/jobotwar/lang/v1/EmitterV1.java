package net.smackem.jobotwar.lang.v1;

import net.smackem.jobotwar.lang.Emitter;
import net.smackem.jobotwar.lang.Instruction;
import net.smackem.jobotwar.lang.OpCode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;

public class EmitterV1 extends Emitter {
    private final Map<String, Integer> locals = new HashMap<>();
    private int labelId = 1;
    private boolean passModifierClause;
    private String lastLoadedSymbol;

    @Override
    public void exitProgram(JobotwarV1Parser.ProgramContext ctx) {
        fixup();
    }

    @Override
    public void exitDeclaration(JobotwarV1Parser.DeclarationContext ctx) {
        for (final TerminalNode id : ctx.ID()) {
            this.locals.put(id.getText(), this.instructions().size());
            emit(OpCode.LD_F64);
        }
    }

    @Override
    public void exitLabel(JobotwarV1Parser.LabelContext ctx) {
        emit(OpCode.LABEL, ctx.ID().getText());
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
            emit(OpCode.LD_GLB, addr);
            symbol = ident;
        } else if (ctx.number() != null) {
            final String literal = ctx.number().getText();
            emit(OpCode.LD_F64, Double.parseDouble(literal));
            symbol = literal;
        } else if (ctx.register() != null) {
            final String register = ctx.register().getText();
            emit(OpCode.LD_REG, register);
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
                emit(OpCode.OR);
                break;
            case "and":
                emit(OpCode.AND);
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
                emit(OpCode.EQ);
                break;
            case "!=":
                emit(OpCode.NEQ);
                break;
            case ">":
                emit(OpCode.GT);
                break;
            case ">=":
                emit(OpCode.GE);
                break;
            case "<":
                emit(OpCode.LT);
                break;
            case "<=":
                emit(OpCode.LE);
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
    public void exitProduct(JobotwarV1Parser.ProductContext ctx) {
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
    public void exitMolecule(JobotwarV1Parser.MoleculeContext ctx) {
        if (ctx.func() == null) {
            return;
        }
        final String funcName = ctx.func().getText();
        if ("not".equals(funcName)) {
            emit(OpCode.NOT);
        } else {
            emit(OpCode.INVOKE, funcName);
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
        emit(OpCode.LABEL, "@" + this.labelId);
        this.labelId++;
        this.passModifierClause = false;
    }

    @Override
    public void enterIfClause(JobotwarV1Parser.IfClauseContext ctx) {
        setDisabled(this.passModifierClause);
    }

    @Override
    public void enterUnlessClause(JobotwarV1Parser.UnlessClauseContext ctx) {
        setDisabled(this.passModifierClause);
    }

    @Override
    public void exitIfClause(JobotwarV1Parser.IfClauseContext ctx) {
        emit(OpCode.BR_ZERO, "@" + this.labelId);
        setDisabled(false);
    }

    @Override
    public void exitUnlessClause(JobotwarV1Parser.UnlessClauseContext ctx) {
        emit(OpCode.NOT);
        emit(OpCode.BR_ZERO, "@" + this.labelId);
        setDisabled(false);
    }

    @Override
    public void exitGotoStatement(JobotwarV1Parser.GotoStatementContext ctx) {
        emit(OpCode.BR, ctx.ID().getText());
    }

    @Override
    public void exitAssignStatement(JobotwarV1Parser.AssignStatementContext ctx) {
        for (int i = 0; i < ctx.assignTarget().size() - 1; i++) {
            emit(OpCode.DUP);
        }
        for (final JobotwarV1Parser.AssignTargetContext assignTarget : ctx.assignTarget()) {
            if (assignTarget.register() != null) {
                final String ident = assignTarget.register().getText();
                emit(OpCode.ST_REG, ident);
                this.lastLoadedSymbol = ident;
                continue;
            }
            if (assignTarget.ID() != null) {
                final String ident = assignTarget.ID().getText();
                final Integer addr = this.locals.get(ident);
                if (addr == null) {
                    throw new RuntimeException("Unknown local " + assignTarget.ID().getText());
                }
                emit(OpCode.ST_GLB, addr);
                this.lastLoadedSymbol = ident;
                continue;
            }
            if (assignTarget.specialAssignTarget() != null) {
                if (assignTarget.specialAssignTarget().OUT() != null) {
                    emit(OpCode.LOG, this.lastLoadedSymbol);
                    continue;
                }
            }
            throw new RuntimeException("Unsupported assign target");
        }
    }

    @Override
    public void exitGosubStatement(JobotwarV1Parser.GosubStatementContext ctx) {
        emit(OpCode.CALL, ctx.ID().getText());
    }

    @Override
    public void exitEndsubStatement(JobotwarV1Parser.EndsubStatementContext ctx) {
        emit(OpCode.RET);
    }

    private void fixup() {
        final Map<String, Integer> labelIndices = new HashMap<>();
        int index = 0;
        for (final Instruction instr : this.instructions()) {
            if (instr.opCode() == OpCode.LABEL) {
                labelIndices.put(instr.strArg(), index);
                instr.setIntArg(index);
            }
            index++;
        }
        this.instructions().stream()
                .filter(instr -> instr.opCode().isBranch())
                .forEach(instr -> {
                    final Integer target = labelIndices.get(instr.strArg());
                    if (target == null) {
                        throw new RuntimeException("Unknown label: " + instr.strArg());
                    }
                    instr.setIntArg(target);
                });
    }
}
