package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

public interface BeanRepository<T extends PersistableBean> {

    /**
     * Selects the beans matching the specified query from the repository.
     *
     * @param query The query that is used to describe the result set.
     * @return A {@link Stream} of beans.
     * @throws ParseException If there was problem translating the {@code query} to the representation
     *      required by the repository implementation.
     */
    @NotNull Stream<T> select(@NotNull Query query) throws ParseException;

    /**
     * Gets the beans with the specified ids from the repo.
     *
     * @param ids The ids to look up.
     * @return A list containing the beans with the specified ids. Only the beans found in the repository
     *      are returned, so an empty list means that none was found.
     */
    @NotNull List<T> get(@NotNull String... ids);

    /**
     * Puts the specified bean into the repository.
     *
     * @param bean The bean to accept.
     * @throws ConstraintViolationException if the new bean violates a constraint of the repository implementation.
     */
    void put(@NotNull T bean) throws ConstraintViolationException;

    /**
     * Updates the bean with the given id.
     *
     * @param bean The bean to update.
     * @throws NoSuchBeanException if no bean with id of the specified bean was found.
     */
    void update(@NotNull T bean) throws NoSuchBeanException;

    /**
     * Deletes the bean with the given id from the repository.
     *
     * @param id The id of the bean to delete.
     * @return {@code true} if the bean with the given id was deleted.
     */
    boolean delete(@NotNull String id);

    /**
     * @return The total number of beans in the repository.
     */
    long count();
}
