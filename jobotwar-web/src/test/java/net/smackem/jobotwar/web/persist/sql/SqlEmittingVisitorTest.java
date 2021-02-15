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
        assertThat(whereClause).isEqualTo("test.name = 'Jim' or test.familyName <> 'Murray'");
    }

    @Test
    public void mixedPropertiesAndOr() throws ParseException {
        final String source = """
                (name eq 'Jim' or familyName ne 'Murray') and (age gt 20 or heightMeters gt 2.0)
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("(test.name = 'Jim' or test.familyName <> 'Murray') and (test.age > 20 or test.heightMeters > 2.0)");
    }

    @Test
    public void stringMatchesPartialStar() throws ParseException {
        final String source = """
                name match 'B*'
                """;
        final String whereClause = PQueryCompiler.compile(source, new SqlEmittingVisitor("test"));
        assertThat(whereClause).isEqualTo("test.name like 'B%'");
    }
}