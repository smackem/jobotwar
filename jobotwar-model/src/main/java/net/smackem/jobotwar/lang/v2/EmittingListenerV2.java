package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.common.Emitter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class EmittingListenerV2 extends JobotwarV2BaseListener {

    private static final Logger log = LoggerFactory.getLogger(DeclarationsExtractor.class);
    private final Emitter emitter;
    private final DeclarationsExtractor declarations;
    private final Collection<String> semanticErrors = new ArrayList<>();
    private ProcedureDecl currentProcedure;

    EmittingListenerV2(Emitter emitter, DeclarationsExtractor declarations) {
        this.emitter = Objects.requireNonNull(emitter);
        this.declarations = declarations;
    }

    public Collection<String> semanticErrors() {
        return this.semanticErrors;
    }

    @Override
    public void enterProgram(JobotwarV2Parser.ProgramContext ctx) {
        int address = 0;
        final List<VariableDecl> globals = new ArrayList<>(this.declarations.globals.values());
        globals.sort(Comparator.comparingInt(a -> a.order));

        for (final VariableDecl variable : globals) {
            emitter.emit(OpCode.LD_F64, 0.0);
            variable.setAddress(address);
            address++;
        }

        if (declarations.states.containsKey(StateDecl.MAIN_STATE_NAME) == false) {
            this.semanticErrors.add(String.format("no state '%s' declared!", StateDecl.MAIN_STATE_NAME));
        } else {
            emitter.emit(OpCode.CALL, StateDecl.MAIN_STATE_NAME);
        }
    }

    @Override
    public void exitDeclarator(JobotwarV2Parser.DeclaratorContext ctx) {
        final String ident = ctx.Ident().getText();
        if (ctx.expression() != null) {
            if (emitStoreVariable(ident) == false) {
                logSemanticError(ctx, "unknown variable '" + ident + "'");
            }
        }
    }

    @Override
    public void enterStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        final String ident = ctx.Ident().getText();
        this.currentProcedure = this.declarations.states.get(ident);
        this.emitter.emit(OpCode.LABEL, ident);
        for (final String ignored : this.currentProcedure.locals()) {
            this.emitter.emit(OpCode.LD_F64, 0.0);
        }
    }

    @Override
    public void exitStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        this.currentProcedure = null;
    }

    @Override
    public void enterFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        final String ident = ctx.Ident().getText();
        this.currentProcedure = this.declarations.functions.get(ident);
        this.emitter.emit(OpCode.LABEL, ident);
        for (final String ignored : this.currentProcedure.locals()) {
            this.emitter.emit(OpCode.LD_F64, 0.0);
        }
    }

    @Override
    public void exitFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        this.currentProcedure = null;
    }

    @Override
    public void exitAssignStmt(JobotwarV2Parser.AssignStmtContext ctx) {
        final String ident = ctx.lvalue().Ident().getText();
        if (emitStoreVariable(ident) == false) {
            logSemanticError(ctx, "unknown variable '" + ident + "'");
        }
    }

    @Override
    public void exitReturnStmt(JobotwarV2Parser.ReturnStmtContext ctx) {
        if (this.currentProcedure instanceof FunctionDecl == false) {
            logSemanticError(ctx, "can only return from function!");
            return;
        }
        if (ctx.expression() != null) {
            this.emitter.emit(OpCode.RET_VAL);
        } else {
            this.emitter.emit(OpCode.RET);
        }
    }

    @Override
    public void exitCondition(JobotwarV2Parser.ConditionContext ctx) {
        final var operator = ctx.conditionOperator();
        if (operator == null) {
            return;
        }
        if (operator.And() != null) {
            this.emitter.emit(OpCode.AND);
        } else if (operator.Or() != null) {
            this.emitter.emit(OpCode.OR);
        }
    }

    @Override
    public void exitComparison(JobotwarV2Parser.ComparisonContext ctx) {
        final var operator = ctx.comparator();
        if (operator == null) {
            return;
        }
        if (operator.Eq() != null) {
            this.emitter.emit(OpCode.EQ);
        } else if (operator.Ne() != null) {
            this.emitter.emit(OpCode.NEQ);
        } else if (operator.Gt() != null) {
            this.emitter.emit(OpCode.GT);
        } else if (operator.Lt() != null) {
            this.emitter.emit(OpCode.LT);
        } else if (operator.Ge() != null) {
            this.emitter.emit(OpCode.GE);
        } else if (operator.Le() != null) {
            this.emitter.emit(OpCode.LE);
        }
    }

    @Override
    public void exitTerm(JobotwarV2Parser.TermContext ctx) {
        final var operator = ctx.termOperator();
        if (operator == null) {
            return;
        }
        if (operator.Plus() != null) {
            this.emitter.emit(OpCode.ADD);
        } else if (operator.Minus() != null) {
            this.emitter.emit(OpCode.SUB);
        }
    }

    @Override
    public void exitProduct(JobotwarV2Parser.ProductContext ctx) {
        final var operator = ctx.productOperator();
        if (operator == null) {
            return;
        }
        if (operator.Times() != null) {
            this.emitter.emit(OpCode.MUL);
        } else if (operator.Div() != null) {
            this.emitter.emit(OpCode.DIV);
        } else if (operator.Mod() != null) {
            this.emitter.emit(OpCode.MOD);
        }
    }

    @Override
    public void exitAtom(JobotwarV2Parser.AtomContext ctx) {
        if (ctx.functionCall() != null) {
            emitFunctionCallAtom(ctx.functionCall());
        } else if (ctx.member() != null) {
            emitMemberAtom(ctx.member());
        } else if (ctx.literal() != null) {
            emitLiteralAtom(ctx.literal());
        } else if (ctx.Ident() != null) {
            final String ident = ctx.Ident().getText();
            if (emitLoadVariable(ident) == false) {
                logSemanticError(ctx, "unknown variable '" + ident + "'");
            }
        }
    }

    private void emitFunctionCallAtom(JobotwarV2Parser.FunctionCallContext ctx) {
        final String ident = ctx.Ident().getText();
        switch (ident) {
            case "abs":
            case "tan":
            case "sin":
            case "cos":
            case "atan":
            case "asin":
            case "acos":
            case "sqrt":
            case "trunc":
                this.emitter.emit(OpCode.INVOKE, ident);
                return;
            case "not":
                this.emitter.emit(OpCode.NOT);
                return;
        }
        final FunctionDecl function = this.declarations.functions.get(ident);
        if (function == null) {
            logSemanticError(ctx, "unkown function '" + ident + "'");
        }
        this.emitter.emit(OpCode.CALL, ident);
    }

    private void emitMemberAtom(JobotwarV2Parser.MemberContext ctx) {
        switch (ctx.functionCall().Ident().getText()) {
            case "speed":
            case "random":
            case "radar":
            case "fire":
            case "damage":
            case "x":
            case "y":
                break;
        }
    }

    private void emitLiteralAtom(JobotwarV2Parser.LiteralContext ctx) {
        if (ctx.bool() != null) {
            if (ctx.bool().True() != null) {
                this.emitter.emit(OpCode.LD_F64, 1.0);
            } else if (ctx.bool().False() != null) {
                this.emitter.emit(OpCode.LD_F64, 0.0);
            }
        } else if (ctx.number() != null) {
            final double d = Double.parseDouble(ctx.number().getText());
            this.emitter.emit(OpCode.LD_F64, d);
        }
    }

    private boolean emitLoadVariable(String ident) {
        // local?
        if (this.currentProcedure != null) {
            final int index = this.currentProcedure.findLocalOrParameter(ident);
            if (index >= 0) {
                emitter.emit(OpCode.LD_LOC, index);
                return true;
            }
        }
        // global?
        VariableDecl variable = this.declarations.globals.get(ident);
        if (variable != null) {
            emitter.emit(OpCode.LD_GLB, variable.getAddress());
            return true;
        }
        // state param?
        if (this.currentProcedure instanceof StateDecl == false) {
            return false;
        }
        ident = DeclarationsExtractor.getStateParameterName(this.currentProcedure.name, ident);
        variable = this.declarations.globals.get(ident);
        if (variable != null) {
            emitter.emit(OpCode.LD_GLB, variable.getAddress());
            return true;
        }
        return false;
    }

    private boolean emitStoreVariable(String ident) {
        if (this.currentProcedure != null) {
            final int index = this.currentProcedure.findLocalOrParameter(ident);
            if (index >= 0) {
                emitter.emit(OpCode.ST_LOC, index);
                return true;
            }
        }
        final VariableDecl variable = this.declarations.globals.get(ident);
        if (variable != null) {
            emitter.emit(OpCode.ST_GLB, variable.getAddress());
            return true;
        }
        return false;
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
