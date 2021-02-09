package net.smackem.jobotwar.web.persist.memory;

import java.util.Objects;
import java.util.function.UnaryOperator;

@SuppressWarnings("ClassCanBeRecord")
class UnaryFilter implements Filter {

    private final Filter innerFilter;
    private final UnaryOperator<Boolean> operator;

    UnaryFilter(Filter innerFilter, UnaryOperator<Boolean> operator) {
        this.innerFilter = Objects.requireNonNull(innerFilter);
        this.operator = Objects.requireNonNull(operator);
    }

    @Override
    public boolean matches(Object bean) {
        return this.operator.apply(this.innerFilter.matches(bean));
    }
}
