package net.smackem.jobotwar.web.persist.memory;

import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.persist.RobotDao;
import net.smackem.jobotwar.web.query.Query;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryRobotDao implements RobotDao {
    private final BeanRepository<RobotBean> repository = new InMemoryBeanRepository<>();

    @Override
    public Stream<RobotBean> select(Query query) throws ParseException {
        return this.repository.select(query);
    }

    @Override
    public List<RobotBean> get(String... ids) {
        return this.repository.get(ids);
    }

    @Override
    public void put(RobotBean bean) throws ConstraintViolationException {
        this.repository.put(bean);
    }

    @Override
    public void update(RobotBean bean) throws NoSuchBeanException {
        this.repository.update(bean);
    }

    @Override
    public Optional<RobotBean> delete(String id) {
        return this.repository.delete(id);
    }

    @Override
    public long count() {
        return this.repository.count();
    }
}
