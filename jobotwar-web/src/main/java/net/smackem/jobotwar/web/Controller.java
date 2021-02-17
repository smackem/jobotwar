package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.query.Query;

class Controller {
    private final long selectedRowCountLimit;

    Controller(long selectedRowCountLimit) {
        this.selectedRowCountLimit = selectedRowCountLimit;
    }

    long selectedRowCountLimit() {
        return this.selectedRowCountLimit;
    }

    Query createQuery(Context ctx) {
        final Query.Builder builder = new Query.Builder();
        final String filterSource = ctx.queryParam("filter");
        if (filterSource != null) {
            builder.filterSource(filterSource);
        }
        final String offset = ctx.queryParam("offset");
        if (offset != null) {
            builder.offset(Long.parseLong(offset));
        }
        final String limit = ctx.queryParam("limit");
        if (limit != null) {
            builder.limit(Math.min(Long.parseLong(limit), this.selectedRowCountLimit));
        } else {
            builder.limit(this.selectedRowCountLimit);
        }
        return builder.build();
    }
}
