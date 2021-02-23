package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.beans.RobotWinStats;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.persist.RobotDao;
import net.smackem.jobotwar.web.query.PQueryCompiler;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.ParseException;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class SqlRobotDao extends SqlDao implements RobotDao {
    private static final Logger log = LoggerFactory.getLogger(SqlRobotDao.class);

    public SqlRobotDao(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @NotNull
    @Override
    public Collection<RobotBean> select(@NotNull Query query) throws ParseException {
        final String filterSource = query.filterSource();
        final String whereClause = filterSource != null
                ? PQueryCompiler.compile(query.filterSource(), new SqlEmittingVisitor("robot"))
                : "1=1";
        final String offsetClause = query.offset().map(offset -> " offset " + offset).orElse("");
        final String limitClause = query.limit().map(limit -> " limit " + limit).orElse("");
        try (final Connection conn = connect()) {
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery("select * from robot where " + whereClause + offsetClause + limitClause);
            return loadResultSet(rs, SqlRobotDao::loadRobotBean);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @NotNull
    @Override
    public List<RobotBean> get(@NotNull String... ids) {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                select * from robot where id in (%s)
                """.formatted(repeatCommaSeparated("?", ids.length)));
            for (int param = 1; param <= ids.length; param++) {
                stmt.setObject(param, UUID.fromString(ids[param - 1]));
            }
            final ResultSet rs = stmt.executeQuery();
            return loadResultSet(rs, SqlRobotDao::loadRobotBean);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void put(@NotNull RobotBean bean) throws ConstraintViolationException {
        if (bean.isFrozen() == false) {
            throw new IllegalArgumentException("bean must be frozen to be persisted");
        }
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
            if (stmt.executeUpdate() != 1) {
                log.warn("error inserting robot with id " + bean.id());
                throw new ConstraintViolationException("error inserting robot");
            }
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void update(@NotNull RobotBean bean) throws NoSuchBeanException {
        if (bean.isFrozen() == false) {
            throw new IllegalArgumentException("bean must be frozen to be persisted");
        }
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                update robot set
                    name = ?,
                    language = ?, 
                    code = ?,
                    acceleration = ?,
                    rgb = ?,
                    date_modified = ?
                where id = ?
                """);
            stmt.setString(1, bean.name());
            stmt.setString(2, bean.language().toString());
            stmt.setString(3, bean.code());
            stmt.setDouble(4, bean.acceleration());
            stmt.setInt(5, bean.rgb());
            stmt.setObject(6, bean.dateModified());
            stmt.setObject(7, UUID.fromString(bean.id()));
            if (stmt.executeUpdate() == 0) {
                log.warn("no robot found with id " + bean.id());
                throw new NoSuchBeanException("no robot found with id " + bean.id());
            }
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public boolean delete(@NotNull String id) {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                delete from robot where id = ?
                """);
            stmt.setObject(1, UUID.fromString(id));
            return stmt.executeUpdate() != 0;
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public long count() {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                select count(*) from robot
                """);
            final ResultSet rs = stmt.executeQuery();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public Collection<RobotWinStats> getWinStats(Query query) {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                with matches_played as (
                    select r.id as robot_id, r.name as robot_name, count(m) as play_count
                    from match m
                    join match_robot mr on m.id = mr.match_id
                    join robot r on r.id = mr.robot_id
                    group by r.id, r.name
                ), matches_won as (
                    select r.id as robot_id, count(*) as win_count
                    from match m
                    join robot r on r.id = m.winner_id
                    group by robot_id
                )
                select
                    matches_played.robot_id,
                    matches_played.robot_name,
                    matches_played.play_count,
                    matches_won.win_count,
                    matches_won.win_count * 100.0 / matches_played.play_count as win_percent
                from matches_played
                join matches_won on matches_played.robot_id = matches_won.robot_id
                order by win_percent desc
                offset ?
                limit ?
                """);
            stmt.setLong(1, query.offset().orElse(0L));
            stmt.setLong(2, query.limit().orElse(Long.MAX_VALUE));
            final ResultSet rs = stmt.executeQuery();
            return loadResultSet(rs, SqlRobotDao::loadWinStats);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public Optional<RobotWinStats> getWinStats(String robotId) {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                    with robot_info as (
                        select r.id robot_id, r.name robot_name
                        from robot r 
                        where r.id = ?
                    ), matches_played as (
                        select count(m) play_count
                        from match m
                        join match_robot mr on m.id = mr.match_id
                        where mr.robot_id = ?
                    ), matches_won as (
                        select count(m) win_count
                        from match m
                        where m.winner_id = ?
                    )
                    select
                        robot_info.robot_id,
                        robot_info.robot_name,
                        matches_played.play_count,
                        matches_won.win_count,
                        matches_won.win_count * 100.0 / matches_played.play_count as win_percent
                    from robot_info, matches_played, matches_won
                    """);
            final UUID robotUUID = UUID.fromString(robotId);
            stmt.setObject(1, robotUUID);
            stmt.setObject(2, robotUUID);
            stmt.setObject(3, robotUUID);
            final ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(loadWinStats(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    private static RobotWinStats loadWinStats(ResultSet rs) throws SQLException {
        final RobotWinStats bean = new RobotWinStats(rs.getString(1), rs.getString(2));
        bean.playCount(rs.getInt(3));
        bean.winCount(rs.getInt(4));
        bean.winPercent(rs.getDouble(5));
        return bean.freeze();
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
