package net.smackem.jobotwar.lang;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Compiler {

    public void compile() {
        final String source = "" +
                "// hepp\n" +
                "START:\n" +
                "goto START if 100 < 0\n";

        final CharStream input = CharStreams.fromString(source);
        final JobotwarLexer lexer = new JobotwarLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JobotwarParser parser = new JobotwarParser(tokens);
        final JobotwarParser.ProgramContext tree = parser.program();
        final Emitter emitter = new Emitter();
        ParseTreeWalker.DEFAULT.walk(emitter, tree);
    }
}
