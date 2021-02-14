package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.persist.RobotDao;
import net.smackem.jobotwar.web.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SqlRobotDao extends SqlDao implements RobotDao {
    private static final Logger log = LoggerFactory.getLogger(SqlRobotDao.class);

    public SqlRobotDao(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @Override
    public Stream<RobotBean> select(Query query) throws ParseException {
        try (final Connection conn = connect()) {
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery("select * from robot");
            return loadResultSet(rs).stream();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public List<RobotBean> get(String... ids) {
        final StringBuilder sql = new StringBuilder("select * from robot where id in (");
        boolean first = true;
        for (final String id : ids) {
            if (first == false) {
                sql.append(',');
            }
            sql.append("uuid_in('").append(id).append("')");
            first = false;
        }
        sql.append(')');
        try (final Connection conn = connect()) {
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery(sql.toString());
            return loadResultSet(rs);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void put(RobotBean bean) throws ConstraintViolationException {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                insert into robot values(
                    ?, --id
                    ?, --name
                    ?, --language
                    ?, --code
                    ?, --acceleration
                    ?, --rgb
                    ?, --dateCreated
                    null) --dateModified
                """);
            stmt.setObject(1, UUID.fromString(bean.id()));
            stmt.setString(2, bean.name());
            stmt.setString(3, bean.language().toString());
            stmt.setString(4, bean.code());
            stmt.setDouble(5, bean.acceleration());
            stmt.setInt(6, bean.rgb());
            stmt.setObject(7, bean.dateCreated());
            stmt.execute();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void update(RobotBean bean) throws NoSuchBeanException {

    }

    @Override
    public Optional<RobotBean> delete(String id) {
        return Optional.empty();
    }

    @Override
    public long count() {
        return 0;
    }

    private static List<RobotBean> loadResultSet(ResultSet rs) throws SQLException {
        final List<RobotBean> beans = new ArrayList<>();
        while (rs.next()) {
            beans.add(loadRobotBean(rs));
        }
        return beans;
    }

    private static RobotBean loadRobotBean(ResultSet rs) throws SQLException {
        final RobotBean bean = new RobotBean(rs.getString(1))
                .name(rs.getString(2))
                .language(Enum.valueOf(Compiler.Language.class, rs.getString(3)))
                .code(rs.getString(4))
                .acceleration(rs.getDouble(5))
                .rgb(rs.getInt(6));
        final Timestamp dateCreated = rs.getTimestamp(7);
        if (dateCreated != null) {
            bean.dateCreated(dateCreated.toInstant().atOffset(ZoneOffset.UTC));
        }
        final Timestamp dateModified = rs.getTimestamp(8);
        if (dateModified != null) {
            bean.dateModified(dateModified.toInstant().atOffset(ZoneOffset.UTC));
        }
        return bean.freeze();
    }
}
