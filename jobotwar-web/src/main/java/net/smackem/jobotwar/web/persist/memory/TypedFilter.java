package net.smackem.jobotwar.web.persist.memory;

import java.util.Objects;
import java.util.function.Function;

public abstract class TypedFilter<TLeft, TRight> implements Filter {
    private final Function<Object, Object> left;
    private final Function<Object, Object> right;
    private final Class<TLeft> leftClass;
    private final Class<TRight> rightClass;

    TypedFilter(Function<Object, Object> left,
                Function<Object, Object> right,
                Class<TLeft> leftClass,
                Class<TRight> rightClass) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.leftClass = Objects.requireNonNull(leftClass);
        this.rightClass = Objects.requireNonNull(rightClass);
    }

    @Override
    public final boolean matches(Object bean) {
        final TLeft l = this.leftClass.cast(this.left.apply(bean));
        final TRight r = this.rightClass.cast(this.right.apply(bean));
        return matches(l, r);
    }

    protected abstract boolean matches(TLeft left, TRight right);
}
