package net.smackem.jobotwar.lang.common;

import net.smackem.jobotwar.lang.Program;

import java.util.Collection;

/**
 * Provides compilation functionality. Implement for each supported language.
 */
public interface CompilerService {
    /**
     * Compiles source code of a implementation-specific language to a {@link Program}.
     * @param source The source code in the implementation-specific language.
     * @param outErrors Receives the compilation errors. Compilation was successful,
     *                  if the collection is empty after the call has returned.
     * @return A new {@link Program}, which might be invalid if errors have occurred.
     */
    Program compile(String source, Collection<String> outErrors);
}
