package net.smackem.jobotwar.web.persist;

import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.beans.RobotWinStats;
import net.smackem.jobotwar.web.query.Query;

import java.util.Collection;
import java.util.Optional;

public interface RobotDao extends BeanRepository<RobotBean> {
    Collection<RobotWinStats> getWinStats(Query query);
    Optional<RobotWinStats> getWinStats(String robotId);
    Collection<RobotWinStats> getWinStatsVsCount(int count, Query query);
    Optional<RobotWinStats> getWinStatsVsCount(int count, String robotId);
}
