package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.common.Emitter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class EmittingListenerV2 extends JobotwarV2BaseListener {

    private static final Logger log = LoggerFactory.getLogger(DeclarationsExtractor.class);
    private static final int STATE_ID_GLB = 0;
    private static final String END_LABEL = "@end";
    private final Emitter emitter;
    private final DeclarationsExtractor declarations;
    private final Collection<String> semanticErrors = new ArrayList<>();
    private ProcedureDecl currentProcedure;
    private boolean emitOnlyLocalDeclarators = false;
    private int labelNumber = 1;

    EmittingListenerV2(Emitter emitter, DeclarationsExtractor declarations) {
        this.emitter = Objects.requireNonNull(emitter);
        this.declarations = declarations;
    }

    public Collection<String> semanticErrors() {
        return this.semanticErrors;
    }

    @Override
    public void enterProgram(JobotwarV2Parser.ProgramContext ctx) {
        // def state_id, initially main
        emitter.emit(OpCode.LD_F64, this.declarations.states.get(StateDecl.MAIN_STATE_NAME).order);
        // def all globals
        int address = 1;
        final List<VariableDecl> globals = new ArrayList<>(this.declarations.globals.values());
        globals.sort(Comparator.comparingInt(a -> a.order));
        for (final VariableDecl variable : globals) {
            emitter.emit(OpCode.LD_F64, 0.0);
            variable.setAddress(address);
            address++;
        }
        // walk all variable declarations to emit initializations
        for (final JobotwarV2Parser.DeclarationContext declCtx : ctx.declaration()) {
            final JobotwarV2Parser.VariableDeclContext varCtx = declCtx.variableDecl();
            if (varCtx != null) {
                ParseTreeWalker.DEFAULT.walk(this, varCtx);
            }
        }
        // emit main loop
        final int mainLoopPC = this.emitter.instructions().size();
        for (final StateDecl state : this.declarations.states.values()) {
            final String label = nextLabel();
            this.emitter.emit(OpCode.LD_GLB, STATE_ID_GLB);
            this.emitter.emit(OpCode.LD_F64, (double)state.order);
            this.emitter.emit(OpCode.EQ);
            this.emitter.emit(OpCode.BR_ZERO, label);
            emitCall(state);
            this.emitter.emit(OpCode.LABEL, label);
        }
        this.emitter.emit(OpCode.BR, mainLoopPC);
        this.emitOnlyLocalDeclarators = true;
    }

    @Override
    public void exitProgram(JobotwarV2Parser.ProgramContext ctx) {
        this.emitter.emit(OpCode.LABEL, END_LABEL);
    }

    @Override
    public void enterDeclarator(JobotwarV2Parser.DeclaratorContext ctx) {
        if (this.emitOnlyLocalDeclarators && this.currentProcedure == null) {
            this.emitter.setDisabled(true);
        }
    }

    @Override
    public void exitDeclarator(JobotwarV2Parser.DeclaratorContext ctx) {
        this.emitter.setDisabled(false);
        if (this.emitOnlyLocalDeclarators && this.currentProcedure == null) {
            return;
        }
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
        if (ctx.statement().isEmpty() || ctx.statement(ctx.statement().size() - 1).yieldStmt() == null) {
            this.emitter.emit(OpCode.RET);
        }
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
        boolean missingReturn = false;
        if (ctx.statement().isEmpty()) {
            missingReturn = true;
        } else if (ctx.statement(ctx.statement().size() - 1).returnStmt() == null) {
            missingReturn = true;
        }
        if (missingReturn) {
            logSemanticError(ctx, "function must return a value");
        }
        this.currentProcedure = null;
    }

    @Override
    public void exitAssignStmt(JobotwarV2Parser.AssignStmtContext ctx) {
        final String ident = ctx.lvalue().Ident().getText();
        if (emitStoreVariable(ident) == false) {
            logSemanticError(ctx, "unknown variable '" + ident + "'");
        }
        this.emitter.emit(OpCode.LABEL, nextLabel());
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
    public void exitMemberStmt(JobotwarV2Parser.MemberStmtContext ctx) {
        final var functionCall = ctx.member().functionCall();
        switch (functionCall.Ident().getText()) {
            case "speed":
                if (functionCall.arguments().expression().size() != 2) {
                    logSemanticError(functionCall, ctx.getText() + " requires 2 arguments");
                }
                this.emitter.emit(OpCode.ST_REG, "SPEEDY");
                this.emitter.emit(OpCode.ST_REG, "SPEEDX");
                break;
            case "speedX":
                if (functionCall.arguments().expression().size() != 1) {
                    logSemanticError(functionCall, ctx.getText() + " requires 1 arguments");
                }
                this.emitter.emit(OpCode.ST_REG, "SPEEDX");
                break;
            case "speedY":
                if (functionCall.arguments().expression().size() != 1) {
                    logSemanticError(functionCall, ctx.getText() + " requires 1 arguments");
                }
                this.emitter.emit(OpCode.ST_REG, "SPEEDY");
                break;
            case "radar":
                if (functionCall.arguments().expression().size() != 1) {
                    logSemanticError(functionCall, ctx.getText() + " requires 1 arguments");
                }
                this.emitter.emit(OpCode.ST_REG, "RADAR");
                break;
            case "fire":
                if (functionCall.arguments().expression().size() != 2) {
                    logSemanticError(functionCall, ctx.getText() + " requires 2 arguments");
                }
                this.emitter.emit(OpCode.SWAP);
                this.emitter.emit(OpCode.ST_REG, "AIM");
                this.emitter.emit(OpCode.ST_REG, "SHOT");
                break;
            case "random":
            case "damage":
            case "x":
            case "y":
                logSemanticError(functionCall, ctx.getText() + " cannot be written to");
                break;
            default:
                logSemanticError(ctx, "unknown register '" + functionCall.Ident().getText() + "'");
                break;
        }
    }

    @Override
    public void exitYieldStmt(JobotwarV2Parser.YieldStmtContext ctx) {
        final var functionCall = ctx.functionCall();
        final String ident = functionCall.Ident().getText();
        final StateDecl state = this.declarations.states.get(ident);
        if (state == null) {
            logSemanticError(functionCall, "unknown state '" + ident + "'");
            return;
        }
        final List<String> parameters = state.parameters();
        for (int i = parameters.size() - 1; i >= 0; i--) {
            final String global = DeclarationsExtractor.getStateParameterName(ident, parameters.get(i));
            final VariableDecl variable = this.declarations.globals.get(global);
            if (variable == null) {
                logSemanticError(ctx, "unknown state parameter '" + parameters.get(i) + "'");
                return;
            }
            emitter.emit(OpCode.ST_GLB, variable.getAddress());
        }
        this.emitter.emit(OpCode.LD_F64, (double)state.order);
        this.emitter.emit(OpCode.ST_GLB, STATE_ID_GLB);
        this.emitter.emit(OpCode.RET);
    }

    @Override
    public void exitExitStmt(JobotwarV2Parser.ExitStmtContext ctx) {
        this.emitter.emit(OpCode.BR, END_LABEL);
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
            return;
        }
        emitCall(function);
    }

    private void emitCall(ProcedureDecl function) {
        if (function instanceof StateDecl) {
            this.emitter.emit(OpCode.LD_F64, function.parameters().size());
        } else {
            this.emitter.emit(OpCode.LD_F64, 0);
        }
        this.emitter.emit(OpCode.CALL, function.name);
    }

    private void emitMemberAtom(JobotwarV2Parser.MemberContext ctx) {
        final var functionCall = ctx.functionCall();
        final String ident = functionCall.Ident().getText();
        switch (ident) {
            case "speedX":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "SPEEDX");
                break;
            case "speedY":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "SPEEDY");
                break;
            case "random":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "RANDOM");
                break;
            case "radar":
                if (functionCall.arguments().expression().size() > 1) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 or 1 arguments");
                }
                if (functionCall.arguments().expression().size() == 1) {
                    this.emitter.emit(OpCode.ST_REG, "RADAR");
                }
                this.emitter.emit(OpCode.LD_REG, "RADAR");
                break;
            case "fire":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "SHOT");
                break;
            case "damage":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "DAMAGE");
                break;
            case "x":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "X");
                break;
            case "y":
                if (functionCall.arguments().expression().size() != 0) {
                    logSemanticError(functionCall, "@" + ident + " requires 0 arguments");
                }
                this.emitter.emit(OpCode.LD_REG, "Y");
                break;
            default:
                logSemanticError(ctx, "unknown register '" + ident + "'");
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
        VariableDecl variable = this.declarations.globals.get(ident);
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

    private String nextLabel() {
        final String label = "@lbl" + this.labelNumber;
        this.labelNumber++;
        return label;
    }
}
