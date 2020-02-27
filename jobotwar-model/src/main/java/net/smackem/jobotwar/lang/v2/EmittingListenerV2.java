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

        int address = 0;
        final List<VariableDecl> globals = new ArrayList<>(this.declarations.globals.values());
        globals.sort(Comparator.comparingInt(a -> a.order));

        for (final VariableDecl variable : globals) {
            emitter.emit(OpCode.LD_F64, 0.0);
            variable.setAddress(address);
            address++;
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
        this.currentProcedure = this.declarations.states.get(ctx.Ident().getText());
        this.currentProcedure.setAddress(this.emitter.instructions().size());
    }

    @Override
    public void exitStateDecl(JobotwarV2Parser.StateDeclContext ctx) {
        this.currentProcedure = null;
    }

    @Override
    public void enterFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        this.currentProcedure = this.declarations.functions.get(ctx.Ident().getText());
        this.currentProcedure.setAddress(this.emitter.instructions().size());
    }

    @Override
    public void exitFunctionDecl(JobotwarV2Parser.FunctionDeclContext ctx) {
        this.currentProcedure = null;
    }

    private boolean emitLoadVariable(String ident) {
        if (this.currentProcedure != null) {
            final int index = this.currentProcedure.findLocalOrParameter(ident);
            if (index >= 0) {
                emitter.emit(OpCode.LD_LOC, index);
                return true;
            }
        }
        final VariableDecl variable = this.declarations.globals.get(ident);
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
