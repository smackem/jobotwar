package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.query.PQueryCompiler;
import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterTest {
    @Test
    public void stringPropertyEq() throws ParseException {
        final String source = """
                name eq 'Bob'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringPropertyEqFalse() throws ParseException {
        final String source = """
                name eq 'Jim'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isFalse();
    }

    @Test
    public void stringPropertyEqOrNe() throws ParseException {
        final String source = """
                name eq 'Jim' or familyName ne 'Murray'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void mixedPropertiesAndOr() throws ParseException {
        final String source = """
                (name eq 'Jim' or familyName ne 'Murray') and (age gt 20 or heightMeters gt 2.0)
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringMatchesPartialStar() throws ParseException {
        final String source = """
                name match 'B*'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringMatchesFullStar() throws ParseException {
        final String source = """
                name match '*'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringMatchesNoCase() throws ParseException {
        final String source = """
                name match 'bob'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringMatchesQuestionMark() throws ParseException {
        final String source = """
                name match 'b?b'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringDoesNotMatch() throws ParseException {
        final String source = """
                not(name match 'jim*')
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringDoesNotMatch2() throws ParseException {
        final String source = """
                name match 'jim*'
                """;
        final Filter filter = PQueryCompiler.compile(source, new FilterEmittingVisitor());
        assertThat(filter.matches(new TestBean())).isFalse();
    }

    private static class TestBean {
        private final String name = "Bob";
        private final String familyName = "Blob";
        private final int age = 50;
        private final LocalDate dateOfBirth = LocalDate.now().minusYears(50);
        private final double heightMeters = 1.8;
    }
}
