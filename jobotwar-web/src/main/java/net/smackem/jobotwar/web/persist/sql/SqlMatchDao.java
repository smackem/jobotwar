package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.Query;

import java.sql.Connection;
import java.text.ParseException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SqlMatchDao extends SqlDao implements MatchDao {
    public SqlMatchDao(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    public Stream<MatchBean> select(Query query) throws ParseException {
        return null;
    }

    @Override
    public List<MatchBean> get(String... ids) {
        return null;
    }

    @Override
    public void put(MatchBean bean) throws ConstraintViolationException {

    }

    @Override
    public void update(MatchBean bean) throws NoSuchBeanException {

    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }
}
