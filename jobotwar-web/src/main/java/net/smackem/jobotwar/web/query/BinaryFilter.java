package net.smackem.jobotwar.web.query;

import java.util.Objects;
import java.util.function.BinaryOperator;

@SuppressWarnings("ClassCanBeRecord")
class BinaryFilter implements Filter {

    private final Filter left, right;
    private final BinaryOperator<Boolean> operator;

    BinaryFilter(Filter left, Filter right, BinaryOperator<Boolean> operator) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.operator = Objects.requireNonNull(operator);
    }

    @Override
    public boolean matches(Object bean) {
        return this.operator.apply(this.left.matches(bean), this.right.matches(bean));
    }
}
