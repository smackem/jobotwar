package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.Query;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryMatchDao implements MatchDao {
    private final BeanRepository<MatchBean> repository = new InMemoryBeanRepository<>();

    @Override
    public Stream<MatchBean> select(Query query) throws ParseException {
        return this.repository.select(query);
    }

    @Override
    public List<MatchBean> get(String... ids) {
        return this.repository.get(ids);
    }

    @Override
    public void put(MatchBean bean) throws ConstraintViolationException {
        this.repository.put(bean);
    }

    @Override
    public void update(MatchBean bean) throws NoSuchBeanException {
        this.repository.update(bean);
    }

    @Override
    public Optional<MatchBean> delete(String id) {
        return this.repository.delete(id);
    }

    @Override
    public long count() {
        return this.repository.count();
    }
}
