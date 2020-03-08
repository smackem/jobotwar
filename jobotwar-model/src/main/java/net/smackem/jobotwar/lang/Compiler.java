package net.smackem.jobotwar.lang;

import net.smackem.jobotwar.lang.common.CompilerService;
import net.smackem.jobotwar.lang.v1.CompilerV1;
import net.smackem.jobotwar.lang.v2.CompilerV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The compiler that translates jobotwar source code into a {@link Program} that can be executed by an {@link Interpreter}.
 */
public final class Compiler {

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

    /**
     * Identifies the source language to compile.
     */
    public enum Language {
        V1, V2
    }

    /**
     * Compiles the specified {@code source} to an executable program.
     * @param source The jobotwar source code to compile.
     * @return A {@link Result} that contains the compilation result: the compiled program or error messages.
     */
    public Result compile(String source, Language language) {
        final CompilerService compilerService;
        switch (language) {
            case V1:
                compilerService = new CompilerV1();
                break;
            case V2:
                compilerService = new CompilerV2();
                break;
            default:
                throw new IllegalArgumentException("unsupported language: " + language);
        }
        final Collection<String> errors = new ArrayList<>();
        final Program program = compilerService.compile(source, errors);
        return new Result(errors, program);
    }
}
