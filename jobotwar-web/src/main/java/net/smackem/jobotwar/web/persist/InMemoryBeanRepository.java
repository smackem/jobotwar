package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.query.Filter;
import net.smackem.jobotwar.web.query.Query;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InMemoryBeanRepository<T extends PersistableBean> implements BeanRepository<T> {

    private final Map<String, T> map;

    InMemoryBeanRepository() {
        this.map = new ConcurrentHashMap<>();
    }

    InMemoryBeanRepository(Collection<T> beans) {
        this.map = beans.stream().collect(Collectors.toConcurrentMap(PersistableBean::id, bean -> bean));
    }

    @Override
    public Stream<T> select(Query query) {
        Stream<T> result = this.map.values().stream();
        final Filter filter = Objects.requireNonNull(query).filter();
        if (filter != null) {
            result = result.filter(filter::matches);
        }
        final Optional<Long> offset = query.offset();
        if (offset.isPresent()) {
            result = result.skip(offset.get());
        }
        final Optional<Long> limit = query.limit();
        if (limit.isPresent()) {
            result = result.limit(limit.get());
        }
        return result;
    }

    @Override
    public Optional<T> get(String id) {
        return Optional.ofNullable(this.map.get(id));
    }

    @Override
    public void put(T bean) throws ConstraintViolationException {
        Objects.requireNonNull(bean);
        if (bean.isFrozen() == false) {
            throw new IllegalArgumentException("bean must be frozen to be persisted");
        }
        if (this.map.putIfAbsent(bean.id(), bean) != null) {
            throw new ConstraintViolationException("an item with id %s already exists".formatted(bean.id()));
        }
    }

    @Override
    public void update(T bean) throws NoSuchBeanException {
        Objects.requireNonNull(bean);
        if (bean.isFrozen() == false) {
            throw new IllegalArgumentException("bean must be frozen to be persisted");
        }
        if (this.map.replace(bean.id(), bean) == null) {
            throw new NoSuchBeanException("no item with id %s was found".formatted(bean.id()));
        }
    }

    @Override
    public Optional<T> delete(String id) {
        return Optional.ofNullable(this.map.remove(id));
    }

    @Override
    public long count() {
        return this.map.size();
    }
}
