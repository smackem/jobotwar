package net.smackem.jobotwar.lang;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

class Emitter extends JobotwarBaseListener {
    private final List<Instruction> instructions = new ArrayList<>();
    private final Map<String, Integer> locals = new HashMap<>();
    private int labelId = 1;
    private boolean disabled;
    private boolean passModifierClause;

    public List<Instruction> instructions() {
        return Collections.unmodifiableList(this.instructions);
    }

    @Override
    public void exitProgram(JobotwarParser.ProgramContext ctx) {
        fixup();
    }

    @Override
    public void exitDeclaration(JobotwarParser.DeclarationContext ctx) {
        for (final TerminalNode id : ctx.ID()) {
            this.locals.put(id.getText(), this.instructions.size());
            emit(OpCode.LD_F64);
        }
    }

    @Override
    public void exitLabel(JobotwarParser.LabelContext ctx) {
        emit(OpCode.LABEL, ctx.ID().getText());
    }

    @Override
    public void exitAtom(JobotwarParser.AtomContext ctx) {
        if (ctx.ID() != null) {
            final Integer addr = this.locals.get(ctx.ID().getText());
            if (addr == null) {
                throw new RuntimeException("Unknown local " + ctx.ID().getText());
            }
            emit(OpCode.LD_LOC, addr);
        } else if (ctx.number() != null) {
            emit(OpCode.LD_F64, Double.parseDouble(ctx.number().getText()));
        } else if (ctx.register() != null) {
            emit(OpCode.LD_REG, ctx.register().getText());
        }
    }

    @Override
    public void exitCondition(JobotwarParser.ConditionContext ctx) {
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
    public void exitComparison(JobotwarParser.ComparisonContext ctx) {
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
    public void exitMolecule(JobotwarParser.MoleculeContext ctx) {
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
    public void enterStatement(JobotwarParser.StatementContext ctx) {
        if (ctx.ifClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.ifClause());
        } else if (ctx.unlessClause() != null) {
            ParseTreeWalker.DEFAULT.walk(this, ctx.unlessClause());
        }
        this.passModifierClause = true;
    }

    @Override
    public void exitStatement(JobotwarParser.StatementContext ctx) {
        emit(OpCode.LABEL, "@" + this.labelId);
        this.labelId++;
        this.passModifierClause = false;
    }

    @Override
    public void enterIfClause(JobotwarParser.IfClauseContext ctx) {
        this.disabled = this.passModifierClause;
    }

    @Override
    public void enterUnlessClause(JobotwarParser.UnlessClauseContext ctx) {
        this.disabled = this.passModifierClause;
    }

    @Override
    public void exitIfClause(JobotwarParser.IfClauseContext ctx) {
        emit(OpCode.BR_ZERO, "@" + this.labelId);
        this.disabled = false;
    }

    @Override
    public void exitUnlessClause(JobotwarParser.UnlessClauseContext ctx) {
        emit(OpCode.NOT);
        emit(OpCode.BR_ZERO, "@" + this.labelId);
        this.disabled = false;
    }

    @Override
    public void exitGotoStatement(JobotwarParser.GotoStatementContext ctx) {
        emit(OpCode.BR, ctx.ID().getText());
    }

    @Override
    public void exitAssignStatement(JobotwarParser.AssignStatementContext ctx) {
        for (int i = 0; i < ctx.assignTarget().size() - 1; i++) {
            emit(OpCode.DUP);
        }
        for (final JobotwarParser.AssignTargetContext assignTarget : ctx.assignTarget()) {
            if (assignTarget.register() != null) {
                emit(OpCode.ST_REG, assignTarget.register().getText());
            } else if (assignTarget.ID() != null) {
                final Integer addr = this.locals.get(assignTarget.ID().getText());
                if (addr == null) {
                    throw new RuntimeException("Unknown local " + assignTarget.ID().getText());
                }
                emit(OpCode.ST_LOC, addr);
            } else {
                throw new RuntimeException("Unsupported assign target");
            }
        }
    }

    @Override
    public void exitGosubStatement(JobotwarParser.GosubStatementContext ctx) {
        emit(OpCode.CALL, ctx.ID().getText());
    }

    @Override
    public void exitEndsubStatement(JobotwarParser.EndsubStatementContext ctx) {
        emit(OpCode.RET);
    }

    private void emit(OpCode opCode) {
        emit(new Instruction(opCode));
    }

    private void emit(OpCode opCode, int intArg) {
        emit(new Instruction(opCode, intArg));
    }

    private void emit(OpCode opCode, double f64Arg) {
        emit(new Instruction(opCode, f64Arg));
    }

    private void emit(OpCode opCode, String strArg) {
        emit(new Instruction(opCode, strArg));
    }

    private void emit(Instruction instruction) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(instruction);
    }

    private void fixup() {
        final Map<String, Integer> labelIndices = new HashMap<>();
        int index = 0;
        for (final Instruction instr : this.instructions) {
            if (instr.opCode() == OpCode.LABEL) {
                labelIndices.put(instr.strArg(), index);
                instr.setIntArg(index);
            }
            index++;
        }
        this.instructions.stream()
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
