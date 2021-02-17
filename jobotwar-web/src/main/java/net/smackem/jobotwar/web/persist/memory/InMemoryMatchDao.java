package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

public class InMemoryMatchDao implements MatchDao {
    private final BeanRepository<MatchBean> repository = new InMemoryBeanRepository<>();

    @NotNull
    @Override
    public Collection<MatchBean> select(@NotNull Query query) throws ParseException {
        return this.repository.select(query);
    }

    @NotNull
    @Override
    public List<MatchBean> get(@NotNull String... ids) {
        return this.repository.get(ids);
    }

    @Override
    public void put(@NotNull MatchBean bean) throws ConstraintViolationException {
        this.repository.put(bean);
    }

    @Override
    public void update(@NotNull MatchBean bean) throws NoSuchBeanException {
        this.repository.update(bean);
    }

    @Override
    public boolean delete(@NotNull String id) {
        return this.repository.delete(id);
    }

    @Override
    public long count() {
        return this.repository.count();
    }
}
