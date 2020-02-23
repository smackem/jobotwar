package net.smackem.jobotwar.lang.common;

import net.smackem.jobotwar.lang.Program;

import java.util.Collection;

public interface CompilerService {
    Program compile(String source, Collection<String> outErrors);
}
