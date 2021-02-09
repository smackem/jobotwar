package net.smackem.jobotwar.web.persist.memory;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;

@SuppressWarnings("ClassCanBeRecord")
class RelationalFilter implements Filter {

    private final Function<Object, Object> left;
    private final Function<Object, Object> right;
    private final IntFunction<Boolean> matcher;

    RelationalFilter(Function<Object, Object> left,
                     Function<Object, Object> right,
                     IntFunction<Boolean> matcher) {
        this.left = Objects.requireNonNull(left);
        this.right = Objects.requireNonNull(right);
        this.matcher = Objects.requireNonNull(matcher);
    }

    @Override
    public boolean matches(Object bean) {
        Object lvalue = left.apply(bean);
        Object rvalue = right.apply(bean);
        if (lvalue instanceof Enum<?>) {
            lvalue = ((Enum<?>) lvalue).name();
        }
        if (rvalue instanceof Enum<?>) {
            rvalue = ((Enum<?>) rvalue).name();
        }
        if (lvalue instanceof Comparable<?> == false) {
            throw new IllegalArgumentException("type " + lvalue.getClass() + " cannot be matched since it is not Comparable");
        }
        if (lvalue.getClass().isAssignableFrom(rvalue.getClass()) == false) {
            return false;
        }
        //noinspection unchecked
        final int result = ((Comparable<Object>) lvalue).compareTo(rvalue);
        return this.matcher.apply(result);
    }
}
