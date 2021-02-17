package net.smackem.jobotwar.web.persist.sql;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

class SqlDao {
    private static final Logger log = LoggerFactory.getLogger(SqlDao.class);
    private final Supplier<Connection> connectionSupplier;

    SqlDao(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = Objects.requireNonNull(connectionSupplier);
    }

    Connection connect() {
        return this.connectionSupplier.get();
    }

    Transaction beginTransaction(String transactionLabel) throws SQLException {
        return new Transaction(transactionLabel);
    }

    RuntimeException handleSQLException(SQLException e) {
        log.error("database error", e);
        return new RuntimeException(e);
    }

    static <T> List<T> loadResultSet(ResultSet rs, BeanLoader<T> loader) throws SQLException {
        final List<T> beans = new ArrayList<>();
        while (rs.next()) {
            beans.add(loader.load(rs));
        }
        return beans;
    }

    <T> Stream<T> streamQueryResults(String sql, BeanLoader<T> loader) {
        try {
            final Connection conn = connect();
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery(Objects.requireNonNull(sql));
            final Stream<T> stream = Stream.iterate(null,
                    ignored -> {
                        try {
                            return rs.next();
                        } catch (SQLException e) {
                            throw handleSQLException(e);
                        }
                    },
                    ignored -> {
                        try {
                            return loader.load(rs);
                        } catch (SQLException e) {
                            throw handleSQLException(e);
                        }
                    });
            return stream.onClose(() -> {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw handleSQLException(e);
                }
            });
        } catch (SQLException e) {
            throw handleSQLException(e);
        }
    }

    static String repeatCommaSeparated(String s, int count) {
        return CharMatcher.is(',').trimTrailingFrom(Strings.repeat(s + ',', count));
    }

    @FunctionalInterface
    interface BeanLoader<T> {
        T load(ResultSet rs) throws SQLException;
    }

    class Transaction implements AutoCloseable {
        private final String label;
        private final boolean wasAutoCommitEnabled;
        private final Connection connection;
        private boolean complete;

        Transaction(String label) throws SQLException {
            this.label = label;
            this.connection = connect();
            this.wasAutoCommitEnabled = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
        }

        public Connection connection() {
            return this.connection;
        }

        public void complete() {
            this.complete = true;
        }

        @Override
        public void close() {
            try {
                if (this.complete) {
                    log.debug("commit transaction '{}'", this.label);
                    this.connection.commit();
                } else {
                    log.warn("rollback transaction '{}'", this.label);
                    this.connection.rollback();
                }
            } catch (SQLException e) {
                log.error("error committing/rolling back transaction", e);
            }
            try {
                this.connection.setAutoCommit(this.wasAutoCommitEnabled);
            } catch (SQLException e) {
                log.error("error setting autocommit", e);
            }
            try {
                this.connection.close();
            } catch (SQLException e) {
                throw handleSQLException(e);
            }
        }
    }
}
