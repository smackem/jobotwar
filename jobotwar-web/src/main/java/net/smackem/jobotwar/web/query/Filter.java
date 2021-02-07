package net.smackem.jobotwar.web.query;

/**
 * Represents a filter that can be applied to a bean, returning
 * {@code true} if the bean matches the filter.
 */
public interface Filter {
    /**
     * @param bean The bean to test.
     * @return {@code true} if the bean matches the filter.
     */
    boolean matches(Object bean);
}
