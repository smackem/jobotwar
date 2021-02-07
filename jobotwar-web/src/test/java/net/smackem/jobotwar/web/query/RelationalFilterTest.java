package net.smackem.jobotwar.web.query;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationalFilterTest {
    @Test
    public void basics() {
        final Filter trueFilter = new RelationalFilter(
                ignored -> 100,
                ignored -> 100,
                n -> n == 0);
        assertThat(trueFilter.matches(null)).isTrue();
        final Filter falseFilter = new RelationalFilter(
                ignored -> 100,
                ignored -> "x",
                n -> n == 0);
        assertThat(falseFilter.matches(null)).isFalse();
    }
}