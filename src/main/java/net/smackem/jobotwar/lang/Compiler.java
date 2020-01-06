package net.smackem.jobotwar.lang;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;

public class Compiler {

    /**
     * Contains the result of a compilation.
     */
    public static class Result {
        private final Collection<String> errors;
        private final Program program;

        private Result(Collection<String> errors, Program program) {
            this.errors = Collections.unmodifiableCollection(errors);
            this.program = program;
        }

        /**
         * @return {@code true} if there have been errors in the compilation.
         */
        public boolean hasErrors() {
            return this.errors.isEmpty() == false;
        }

        /**
         * @return An unmodifiable collection of error messages.
         */
        public Collection<String> errors() {
            return this.errors;
        }

        /**
         * @return The compiled program, which might be incomplete if {@link #hasErrors()} is {@code true}.
         */
        public Program program() {
            return this.program;
        }
    }

    public Result compile(String source) {
        if (source.isEmpty() == false && source.endsWith("\n") == false) {
            source = source + "\n";
        }
        final ErrorListener errorListener = new ErrorListener();
        final CharStream input = CharStreams.fromString(source);
        final JobotwarLexer lexer = new JobotwarLexer(input);
        lexer.addErrorListener(errorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JobotwarParser parser = new JobotwarParser(tokens);
        parser.addErrorListener(errorListener);
        final JobotwarParser.ProgramContext tree = parser.program();
        final Emitter emitter = new Emitter();
        ParseTreeWalker.DEFAULT.walk(emitter, tree);
        final Program program = new Program(emitter.instructions());
        return new Result(errorListener.errors, program);
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
