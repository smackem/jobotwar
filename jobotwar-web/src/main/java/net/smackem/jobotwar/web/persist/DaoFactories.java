package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.persist.memory.InMemoryMatchDao;
import net.smackem.jobotwar.web.persist.memory.InMemoryRobotDao;

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
        };
    }

    public static DaoFactory sql(Supplier<Connection> connectionSupplier) {
        return null;
    }
}
