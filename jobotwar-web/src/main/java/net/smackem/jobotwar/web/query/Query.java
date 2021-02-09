package net.smackem.jobotwar.web.query;

import java.util.Optional;

/**
 * A query that can be used to filter, limit and sort bean searches.
 */
public class Query {
    private final String filterSource;
    private final long offset;
    private final long limit;

    /**
     * Unconstrained query that returns the full set of beans.
     */
    public static final Query ALL = new Builder().build();

    private Query(Builder builder) {
        this.filterSource = builder.filterSource;
        this.offset = builder.offset;
        this.limit = builder.limit;
    }

    /**
     * Builder class for {@link Query}.
     */
    public static class Builder {
        private String filterSource;
        private long offset = -1;
        private long limit = -1;

        public Builder filterSource(String filterSource) {
            this.filterSource = filterSource;
            return this;
        }

        public Builder limit(long limit) {
            this.limit = limit;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Query build() {
            return new Query(this);
        }
    }

    /**
     * @return The PQuery source code to translate into a filter expression or {@code null}.
     */
    public String filterSource() {
        return this.filterSource;
    }

    public Optional<Long> offset() {
        return this.offset >= 0
                ? Optional.of(this.offset)
                : Optional.empty();
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
