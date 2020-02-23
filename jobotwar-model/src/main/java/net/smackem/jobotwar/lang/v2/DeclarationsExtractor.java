package net.smackem.jobotwar.lang.v2;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class DeclarationsExtractor extends JobotwarV2BaseListener {

    private static final Logger log = LoggerFactory.getLogger(DeclarationsExtractor.class);
    final Map<String, StateDecl> states = new HashMap<>();
    final Map<String, FunctionDecl> functions = new HashMap<>();
    final Map<String, VariableDecl> globals = new HashMap<>();
    final Collection<String> semanticErrors = new ArrayList<>();
    private ProcedureDecl currentProcedure;

    public static String getStateParameterName(String stateName, String parameterName) {
        return stateName + "$" + parameterName;
    }

    @Override
    public void exitProgram(JobotwarV2Parser.ProgramContext ctx) {
        for (final StateDecl state : this.states.values()) {
            for (final String parameter : state.parameters()) {
                final String globalName = getStateParameterName(state.name, parameter);
                final int order = this.globals.size();
                this.globals.put(globalName, new VariableDecl(globalName, order));
            }
        }
    }

    @Override
    public void enterStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        final StateDecl state = new StateDecl(ctx.Ident().getText(), this.states.size());
        if (ctx.parameters() != null) {
            collectParameters(state, ctx.parameters());
        }
        if (this.states.put(state.name, state) != null) {
            logSemanticError(ctx, "duplicate state name '" + state.name + "'");
        }
        this.currentProcedure = state;
    }

    @Override
    public void exitStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        this.currentProcedure = null;
    }

    @Override
    public void enterVariableDecl(JobotwarV2Parser.VariableDeclContext ctx) {
        if (this.currentProcedure != null) {
            for (final JobotwarV2Parser.DeclaratorContext decl : ctx.declarator()) {
                final VariableDecl variable = new VariableDecl(
                        decl.Ident().getText(), this.currentProcedure.locals().size());
                this.currentProcedure.addLocal(variable.name);
            }
            return;
        }
        for (final JobotwarV2Parser.DeclaratorContext decl : ctx.declarator()) {
            final VariableDecl variable = new VariableDecl(decl.Ident().getText(), this.globals.size());
            if (this.globals.put(variable.name, variable) != null) {
                logSemanticError(ctx, "duplicate global variable name '" + variable.name + "'");
            }
        }
    }

    @Override
    public void enterFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        final FunctionDecl function = new FunctionDecl(ctx.Ident().getText(), this.functions.size());
        if (ctx.parameters() != null) {
            collectParameters(function, ctx.parameters());
        }
        if (this.functions.put(function.name, function) != null) {
            logSemanticError(ctx, "duplicate function name '" + function.name + "'");
        }
        this.currentProcedure = function;
    }

    @Override
    public void exitFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        this.currentProcedure = null;
    }

    private void collectParameters(ProcedureDecl procedure, JobotwarV2Parser.ParametersContext ctx) {
        for (final TerminalNode ident : ctx.Ident()) {
            try {
                procedure.addParameter(ident.getText());
            } catch(IllegalArgumentException e) {
                logSemanticError(ctx, e.getMessage());
            }
        }
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
