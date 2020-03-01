package net.smackem.jobotwar.lang.v2;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DeclarationsExtractorTest {

    @Test
    public void testGlobals() {
        final String source = "" +
                "def a, b = 1\n" +
                "def c";
        final DeclarationsExtractor extractor = extract(source);
        assertThat(extractor.semanticErrors).hasSize(1);
        assertThat(extractor.semanticErrors.iterator().next()).contains(StateDecl.MAIN_STATE_NAME);
        assertThat(extractor.functions).isEmpty();
        assertThat(extractor.states).isEmpty();
        assertThat(extractor.globals).hasSize(3);
        assertThat(extractor.globals).containsOnlyKeys("a", "b", "c");
        assertThat(extractor.globals.get("a").order).isEqualTo(0);
        assertThat(extractor.globals.get("b").order).isEqualTo(1);
        assertThat(extractor.globals.get("c").order).isEqualTo(2);
        for (final Map.Entry<String, VariableDecl> entry : extractor.globals.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().name);
        }
    }

    @Test
    public void testStates() {
        final String source = "" +
                "state a() {}\n" +
                "state b() { def l1, l2 }\n" +
                "state c(p1, p2) {}\n" +
                "state main() {}";
        final DeclarationsExtractor extractor = extract(source);
        assertThat(extractor.semanticErrors).isEmpty();
        assertThat(extractor.functions).isEmpty();
        assertThat(extractor.globals).hasSize(2);
        assertThat(extractor.globals.get("c$p1")).isNotNull();
        assertThat(extractor.globals.get("c$p2")).isNotNull();
        assertThat(extractor.states).hasSize(4);
        assertThat(extractor.states).containsOnlyKeys("a", "b", "c", "main");
        assertThat(extractor.states.get("a").order).isEqualTo(0);
        assertThat(extractor.states.get("b").order).isEqualTo(1);
        assertThat(extractor.states.get("c").order).isEqualTo(2);
        assertThat(extractor.states.get("main").order).isEqualTo(3);
        for (final Map.Entry<String, StateDecl> entry : extractor.states.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().name);
        }
        assertThat(extractor.states.get("c").parameters()).hasSize(2);
        assertThat(extractor.states.get("b").locals()).containsExactly("l1", "l2");
    }

    @Test
    public void testFunctions() {
        final String source = "" +
                "def a() {}\n" +
                "def b() { def l1, l2 }\n" +
                "def c(p1, p2) {}";
        final DeclarationsExtractor extractor = extract(source);
        assertThat(extractor.semanticErrors).hasSize(1);
        assertThat(extractor.semanticErrors.iterator().next()).contains(StateDecl.MAIN_STATE_NAME);
        assertThat(extractor.globals).isEmpty();
        assertThat(extractor.states).isEmpty();
        assertThat(extractor.functions).hasSize(3);
        assertThat(extractor.functions).containsOnlyKeys("a", "b", "c");
        assertThat(extractor.functions.get("a").order).isEqualTo(0);
        assertThat(extractor.functions.get("b").order).isEqualTo(1);
        assertThat(extractor.functions.get("c").order).isEqualTo(2);
        for (final Map.Entry<String, FunctionDecl> entry : extractor.functions.entrySet()) {
            assertThat(entry.getKey()).isEqualTo(entry.getValue().name);
        }
        assertThat(extractor.functions.get("c").parameters()).hasSize(2);
        assertThat(extractor.functions.get("b").locals()).containsExactly("l1", "l2");
    }

    @Test
    public void testProgram() {
        final String source = "" +
                "def g\n" +
                "def f() { def x }\n" +
                "state s(p1, p2) { def y }\n" +
                "state main() {}";
        final DeclarationsExtractor extractor = extract(source);
        assertThat(extractor.semanticErrors).isEmpty();
        assertThat(extractor.globals).hasSize(3);
        assertThat(extractor.globals).containsOnlyKeys("g", "s$p1", "s$p2");
        assertThat(extractor.globals.get("g").order).isEqualTo(0);
        assertThat(extractor.globals.get("s$p1").order).isEqualTo(1);
        assertThat(extractor.globals.get("s$p2").order).isEqualTo(2);
        assertThat(extractor.states).hasSize(2);
        assertThat(extractor.states).containsOnlyKeys("s", "main");
        assertThat(extractor.states.get("s").order).isEqualTo(0);
        assertThat(extractor.states.get("main").order).isEqualTo(1);
        assertThat(extractor.states.get("s").locals()).containsExactly("y");
        assertThat(extractor.functions).hasSize(1);
        assertThat(extractor.functions).containsOnlyKeys("f");
        assertThat(extractor.functions.get("f").order).isEqualTo(0);
        assertThat(extractor.functions.get("f").locals()).containsExactly("x");
    }

    @Test
    public void testDuplicates() {
        final String source = "" +
                "def g\n" +
                "def g\n" +
                "state s(p1, p2) {}\n" +
                "state s() {}\n" +
                "def f(p1, p2) {}\n" +
                "def f() {}\n" +
                "state main() {}";
        final DeclarationsExtractor extractor = extract(source);
        assertThat(extractor.semanticErrors).hasSize(3);
        final Iterator<String> iter = extractor.semanticErrors.iterator();
        assertThat(iter.next()).startsWith("line 2");
        assertThat(iter.next()).startsWith("line 4");
        assertThat(iter.next()).startsWith("line 6");
    }

    private DeclarationsExtractor extract(String source) {
        final CharStream input = CharStreams.fromString(source);
        final JobotwarV2Lexer lexer = new JobotwarV2Lexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final JobotwarV2Parser parser = new JobotwarV2Parser(tokens);
        final JobotwarV2Parser.ProgramContext tree = parser.program();
        final DeclarationsExtractor declExtractor = new DeclarationsExtractor();
        ParseTreeWalker.DEFAULT.walk(declExtractor, tree);
        return declExtractor;
    }
}