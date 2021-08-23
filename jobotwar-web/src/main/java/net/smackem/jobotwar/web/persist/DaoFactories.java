package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.persist.memory.InMemoryMatchDao;
import net.smackem.jobotwar.web.persist.memory.InMemoryRobotDao;
import net.smackem.jobotwar.web.persist.sql.SqlMatchDao;
import net.smackem.jobotwar.web.persist.sql.SqlRobotDao;

import java.sql.Connection;
import java.util.function.Supplier;

public class DaoFactories {
    private DaoFactories() {}

    public static DaoFactory inMemory() {
        final RobotDao robotDao = new InMemoryRobotDao();
        final MatchDao matchDao = new InMemoryMatchDao();
        return new DaoFactory() {
            @Override
            public RobotDao getRobotDao() {
                return robotDao;
            }

            @Override
            public MatchDao getMatchDao() {
                return matchDao;
            }

            @Override
            public void close() {}
        };
    }

    public static DaoFactory sql(Supplier<Connection> connectionSupplier, AutoCloseable db) {
        return new DaoFactory() {
            @Override
            public RobotDao getRobotDao() {
                return new SqlRobotDao(connectionSupplier);
            }

            @Override
            public MatchDao getMatchDao() {
                return new SqlMatchDao(connectionSupplier);
            }

            @Override
            public void close() throws Exception {
                db.close();
            }
        };
    }
}
