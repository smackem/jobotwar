package net.smackem.jobotwar.web.persist;

public interface DaoFactory extends AutoCloseable {
    RobotDao getRobotDao();
    MatchDao getMatchDao();
}
