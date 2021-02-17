package net.smackem.jobotwar.web.persist.sql;

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
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

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
        try (final Connection conn = connect()) {
            final PreparedStatement stmt1 = conn.prepareStatement("""
                with m as (select * from match where %s limit ? offset ?)
                select
                    m.id, m.board_width, m.board_height, m.duration_millis, m.max_duration_millis, m.outcome, m.winner_id, m.date_started,
                    mr.robot_id, mr.x_pos, mr.y_pos
                from m
                join match_robot mr on m.id = mr.match_id;
                """.formatted(whereClause));
            stmt1.setLong(1, query.limit().orElse(0L));
            stmt1.setLong(2, query.offset().orElse(Long.MAX_VALUE));
            final ResultSet rs = stmt1.executeQuery();
            return List.of();
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
        return new MatchBean(rs.getString(1));
    }
}
