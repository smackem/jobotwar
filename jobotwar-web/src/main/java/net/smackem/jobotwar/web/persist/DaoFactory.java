package net.smackem.jobotwar.web.persist;

public interface DaoFactory {
    RobotDao getRobotDao();
    MatchDao getMatchDao();
}
