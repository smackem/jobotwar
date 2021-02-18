package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.MatchEvent;
import net.smackem.jobotwar.web.beans.MatchRobot;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.PQueryCompiler;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SqlMatchDao extends SqlDao implements MatchDao {

    public SqlMatchDao(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @NotNull
    @Override
    public Collection<MatchBean> select(@NotNull Query query) throws ParseException {
        final String whereClause = query.filterSource() != null
                ? PQueryCompiler.compile(query.filterSource(), new SqlEmittingVisitor("match"))
                : "1=1";
        final String offsetClause = query.offset().map(offset -> " offset " + offset).orElse("");
        final String limitClause = query.limit().map(limit -> " limit " + limit).orElse("");
        final String matchFullSql = """
                select id, board_width, board_height, duration_millis, max_duration_millis, winner_id, outcome, date_started
                from match
                where
                """ + whereClause + offsetClause + limitClause;
        final String matchIdSql = "select id from match where " + whereClause + offsetClause + limitClause;

        try (final Connection conn = connect()) {
            // load matches
            final PreparedStatement matchStmt = conn.prepareStatement(matchFullSql);
            final ResultSet matchRs = matchStmt.executeQuery();
            final Collection<MatchBean> beans = loadResultSet(matchRs, SqlMatchDao::loadMatchBean);
            final Map<UUID, MatchBean> beanMap = beans.stream().collect(Collectors.toMap(b -> UUID.fromString(b.id()), b -> b));

            // load match robots
            // language=plain
            final PreparedStatement matchRobotStmt = conn.prepareStatement("""
                    with m as (%s)
                    select mr.match_id, mr.robot_id, mr.x_pos, mr.y_pos
                    from m
                    join match_robot mr on m.id = mr.match_id
                    """.formatted(matchIdSql));
            final ResultSet matchRobotRs = matchRobotStmt.executeQuery();
            while (matchRobotRs.next()) {
                final UUID matchId = (UUID) matchRobotRs.getObject(1);
                final UUID robotId = (UUID) matchRobotRs.getObject(2);
                final double x = matchRobotRs.getDouble(3);
                final double y = matchRobotRs.getDouble(4);
                final MatchBean bean = beanMap.get(matchId);
                if (bean != null) {
                    bean.addRobots(new MatchRobot(robotId.toString()).x(x).y(y));
                }
            }

            // load match events
            // language=plain
            final PreparedStatement matchEventStmt = conn.prepareStatement("""
                    with m as (%s)
                    select me.match_id, me.game_time_millis, me.event
                    from m
                    join match_event me on me.match_id = m.id
                    """.formatted(matchIdSql));
            final ResultSet matchEventRs = matchEventStmt.executeQuery();
            while (matchEventRs.next()) {
                final UUID matchId = (UUID) matchEventRs.getObject(1);
                final long gameTimeMillis = matchEventRs.getLong(2);
                final String event = matchEventRs.getString(3);
                final MatchBean bean = beanMap.get(matchId);
                if (bean != null) {
                    bean.addEvents(new MatchEvent(gameTimeMillis, event));
                }
            }

            // freeze matches
            for (final MatchBean bean : beans) {
                bean.freeze();
            }
            return beans;
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @NotNull
    @Override
    public List<MatchBean> get(@NotNull String... ids) {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                    select * from match where id in (%s)
                    """.formatted(repeatCommaSeparated("?", ids.length)));
            for (int param = 1; param <= ids.length; param++) {
                stmt.setObject(param, UUID.fromString(ids[param - 1]));
            }
            final ResultSet rs = stmt.executeQuery();
            return loadResultSet(rs, SqlMatchDao::loadMatchBean);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void put(@NotNull MatchBean bean) throws ConstraintViolationException {
        if (bean.id() == null) {
            throw new IllegalArgumentException("bean must have an id");
        }
        final UUID matchId = UUID.fromString(bean.id());

        try (final Transaction transaction = beginTransaction("insert match " + bean.id())) {
            final Connection conn = transaction.connection();

            // insert match
            final PreparedStatement stmt = conn.prepareStatement("""
                    insert into match
                        (id, board_width, board_height, duration_millis, max_duration_millis, winner_id, outcome, date_started) 
                    values
                        (?, ?, ?, ?, ?, ?, ?, ?)
                    """);
            stmt.setObject(1, matchId);
            stmt.setInt(2, bean.boardWidth());
            stmt.setInt(3, bean.boardHeight());
            stmt.setLong(4, bean.duration().toMillis());
            stmt.setLong(5, bean.maxDuration().toMillis());
            stmt.setObject(6, bean.winnerId() != null ? UUID.fromString(bean.winnerId()) : null);
            stmt.setString(7, bean.outcome().toString());
            stmt.setObject(8, bean.dateStarted());
            stmt.executeUpdate();

            // insert match robots
            final PreparedStatement matchRobotStmt = conn.prepareStatement("""
                    insert into match_robot
                        (robot_id, match_id, x_pos, y_pos)
                    values
                        (?, ?, ?, ?)
                    """);
            for (final MatchRobot matchRobot : bean.robots()) {
                matchRobotStmt.setObject(1, UUID.fromString(matchRobot.robotId()));
                matchRobotStmt.setObject(2, matchId);
                matchRobotStmt.setDouble(3, matchRobot.x());
                matchRobotStmt.setDouble(4, matchRobot.y());
                matchRobotStmt.addBatch();
            }
            matchRobotStmt.executeBatch();

            // insert match events
            final PreparedStatement matchEventStmt = conn.prepareStatement("""
                    insert into match_event
                        (match_id, game_time_millis, event)
                    values
                        (?, ?, ?)
                    """);
            for (final MatchEvent matchEvent : bean.eventLog()) {
                matchEventStmt.setObject(1, matchId);
                matchEventStmt.setLong(2, matchEvent.gameTimeMillis());
                matchEventStmt.setString(3, matchEvent.event());
                matchEventStmt.addBatch();
            }
            matchEventStmt.executeBatch();

            transaction.complete();
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void update(@NotNull MatchBean bean) throws NoSuchBeanException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(@NotNull String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        try (final Connection conn = connect()) {
            final PreparedStatement stmt = conn.prepareStatement("""
                select count(*) from match
                """);
            final ResultSet rs = stmt.executeQuery();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    private static MatchBean loadMatchBean(ResultSet rs) throws SQLException {
        // select id, board_width, board_height, duration_millis, max_duration_millis, winner_id, outcome, date_started
        return new MatchBean(rs.getString(1))
                .boardWidth(rs.getInt(2))
                .boardHeight(rs.getInt(3))
                .duration(Duration.ofMillis(rs.getLong(4)))
                .maxDuration(Duration.ofMillis(rs.getLong(5)))
                .winnerId(rs.getString(6))
                .outcome(Enum.valueOf(SimulationResult.Outcome.class, rs.getString(7)))
                .dateStarted(rs.getTimestamp(8).toInstant().atOffset(ZoneOffset.UTC));
    }
}
