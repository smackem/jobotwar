package net.smackem.jobotwar.web.persist.sql;

import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.Query;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SqlMatchDao extends SqlDao implements MatchDao {
    public SqlMatchDao(Supplier<Connection> connectionSupplier) {
        super(connectionSupplier);
    }

    @NotNull
    @Override
    public Stream<MatchBean> select(@NotNull Query query) throws ParseException {
        return Stream.empty();
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
        try (final Transaction transaction = beginTransaction()) {
            final Connection conn = transaction.connection();
            final PreparedStatement stmt = conn.prepareStatement("""
                insert into match
                    (id, board_width, board_height, duration_millis, max_duration_millis, winner_id, outcome, date_started) 
                values
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """);
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    @Override
    public void update(@NotNull MatchBean bean) throws NoSuchBeanException {
    }

    @Override
    public boolean delete(@NotNull String id) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    private static MatchBean loadMatchBean(ResultSet rs) throws SQLException {
        return new MatchBean(rs.getString(1));
    }
}
