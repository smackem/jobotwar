package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.common.Emitter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class EmittingVisitorV2 extends JobotwarV2BaseVisitor<Void> {
    private static final Logger log = LoggerFactory.getLogger(DeclarationsExtractor.class);
    private static final int STATE_ID_GLB = 0;
    private static final String END_LABEL = "@end";
    private final Emitter emitter;
    private final DeclarationsExtractor declarations;
    private final Collection<String> semanticErrors = new ArrayList<>();
    private int labelNumber = 1;

    EmittingVisitorV2(Emitter emitter, DeclarationsExtractor declarations) {
        this.emitter = Objects.requireNonNull(emitter);
        this.declarations = Objects.requireNonNull(declarations);
    }

    public Collection<String> semanticErrors() {
        return this.semanticErrors;
    }

    @Override
    public Void visitProgram(JobotwarV2Parser.ProgramContext ctx) {
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
                varCtx.accept(this);
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

        // visit all declarations, omitting globals
        for (var decl : ctx.declaration()) {
            if (decl.variableDecl() == null) {
                decl.accept(this);
            }
        }

        this.emitter.emit(OpCode.LABEL, END_LABEL);
        return null;
    }

    private void emitCall(ProcedureDecl function) {
        if (function instanceof StateDecl) {
            this.emitter.emit(OpCode.LD_F64, function.parameters().size());
        } else {
            this.emitter.emit(OpCode.LD_F64, 0);
        }
        this.emitter.emit(OpCode.CALL, function.name);
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
