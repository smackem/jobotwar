package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.beans.PersistableBean;

import java.util.Optional;
import java.util.stream.Stream;

public interface BeanRepository<T extends PersistableBean> {
    Stream<T> select();
    Optional<T> get(String id);
    void put(T bean) throws ConstraintViolationException;
    void update(T bean) throws NoSuchBeanException;
    Optional<T> delete(String id);
    long count();
}
