package net.smackem.jobotwar.web.query;

import java.util.Optional;

/**
 * A query that can be used to filter, limit and sort bean searches.
 */
public class Query {
    private final Filter filter;
    private final long limit;

    /**
     * Unconstrained query that returns the full set of beans.
     */
    public static final Query ALL = new Builder().build();

    private Query(Builder builder) {
        this.filter = builder.filter;
        this.limit = builder.limit;
    }

    /**
     * Builder class for {@link Query}.
     */
    public static class Builder {
        private Filter filter;
        private long limit = -1;

        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Query build() {
            return new Query(this);
        }
    }

    /**
     * @return The {@link Filter} to use or {@code null} if no filter should be applied.
     */
    public Filter filter() {
        return this.filter;
    }

    /**
     * @return The maximum number of hits if present.
     */
    public Optional<Long> limit() {
        return this.limit >= 0
                ? Optional.of(this.limit)
                : Optional.empty();
    }
}
