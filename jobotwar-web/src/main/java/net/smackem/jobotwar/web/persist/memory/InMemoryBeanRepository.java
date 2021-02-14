package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.PQueryCompiler;
import net.smackem.jobotwar.web.query.Query;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InMemoryBeanRepository<T extends PersistableBean> implements BeanRepository<T> {

    private final Map<String, T> map;

    public InMemoryBeanRepository() {
        this.map = new ConcurrentHashMap<>();
    }

    public InMemoryBeanRepository(Collection<T> beans) {
        this.map = beans.stream().collect(Collectors.toConcurrentMap(PersistableBean::id, bean -> bean));
    }

    @Override
    public Stream<T> select(Query query) throws ParseException {
        Stream<T> result = this.map.values().stream();
        final String filterSource = Objects.requireNonNull(query).filterSource();
        if (filterSource != null) {
            final Filter filter = PQueryCompiler.compile(filterSource, new FilterEmittingVisitor());
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
    public List<T> get(String... ids) {
        return Arrays.stream(ids)
                .map(this.map::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
    public boolean delete(String id) {
        return this.map.remove(id) != null;
    }

    @Override
    public long count() {
        return this.map.size();
    }
}
