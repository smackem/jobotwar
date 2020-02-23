package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.lang.common.CompilerService;
import net.smackem.jobotwar.lang.common.Emitter;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public class CompilerV2 implements CompilerService {

    @Override
    public Program compile(String source, Collection<String> outErrors) {
        final CharStream input = CharStreams.fromString(source);
        final JobotwarV2Lexer lexer = new JobotwarV2Lexer(input);
        final ErrorListener errorListener = new ErrorListener();
        lexer.addErrorListener(errorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JobotwarV2Parser parser = new JobotwarV2Parser(tokens);
        parser.addErrorListener(errorListener);
        final JobotwarV2Parser.ProgramContext tree = parser.program();
        final Emitter emitter = new Emitter();
        final EmittingListenerV2 listener = new EmittingListenerV2(emitter);
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        outErrors.addAll(errorListener.errors);
        return emitter.buildProgram();
    }

    private static class ErrorListener implements ANTLRErrorListener {
        private final Collection<String> errors = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int pos, String s, RecognitionException e) {
            this.errors.add(String.format("line %d:%d: %s", line, pos, s));
        }

        @Override
        public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        }

        @Override
        public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        }

        @Override
        public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        }
    }
}
