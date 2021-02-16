package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.persist.RobotDao;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

public class InMemoryRobotDao implements RobotDao {
    private final BeanRepository<RobotBean> repository = new InMemoryBeanRepository<>();

    @NotNull
    @Override
    public Stream<RobotBean> select(@NotNull Query query) throws ParseException {
        return this.repository.select(query);
    }

    @NotNull
    @Override
    public List<RobotBean> get(@NotNull String... ids) {
        return this.repository.get(ids);
    }

    @Override
    public void put(@NotNull RobotBean bean) throws ConstraintViolationException {
        this.repository.put(bean);
    }

    @Override
    public void update(@NotNull RobotBean bean) throws NoSuchBeanException {
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
