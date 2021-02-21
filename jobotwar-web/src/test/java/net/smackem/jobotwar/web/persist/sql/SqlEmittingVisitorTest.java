package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.web.query.PQueryCompiler;
import org.junit.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlEmittingVisitorTest {
    @Test
    public void stringPropertyEq() throws ParseException {
        final String source = """
                name eq 'Bob'
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("test.name = 'Bob'");
    }

    @Test
    public void stringPropertyEqOrNe() throws ParseException {
        final String source = """
                name eq 'Jim' or familyName ne 'Murray'
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("test.name = 'Jim' or test.family_name <> 'Murray'");
    }

    @Test
    public void mixedPropertiesAndOr() throws ParseException {
        final String source = """
                (name eq 'Jim' or familyName ne 'Murray') and (age gt 20 or heightMeters gt 2.0)
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("(test.name = 'Jim' or test.family_name <> 'Murray') and (test.age > 20 or test.height_meters > 2.0)");
    }

    @Test
    public void stringMatchesPartialStar() throws ParseException {
        final String source = """
                name match 'B*'
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("test.name like 'B%'");
    }

    @Test
    public void sanitizeColumn() throws ParseException {
        final String source = """
                identWithNumbers123_andCamelCase eq 'Bob'
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("test.ident_with_numbers123_and_camel_case = 'Bob'");
    }
}