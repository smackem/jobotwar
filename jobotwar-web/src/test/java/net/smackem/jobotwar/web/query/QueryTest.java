package net.smackem.jobotwar.web.query;

import org.junit.Test;

import java.text.ParseException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {
    @Test
    public void stringPropertyEq() throws ParseException {
        final String source = """
                name eq 'Bob'
                """;
        final Filter filter = FilterCompiler.compile(source);
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    @Test
    public void stringPropertyEqFalse() throws ParseException {
        final String source = """
                name eq 'Jim'
                """;
        final Filter filter = FilterCompiler.compile(source);
        assertThat(filter.matches(new TestBean())).isFalse();
    }

    @Test
    public void stringPropertyEqOrNe() throws ParseException {
        final String source = """
                name eq 'Jim' or familyName ne 'Murray'
                """;
        final Filter filter = FilterCompiler.compile(source);
        assertThat(filter.matches(new TestBean())).isTrue();
    }

    private static class TestBean {
        private final String name = "Bob";
        private final String familyName = "Blob";
        private final int age = 50;
        private final LocalDate dateOfBirth = LocalDate.now().minusYears(50);
        private final double heightMeters = 1.8;
    }
}
