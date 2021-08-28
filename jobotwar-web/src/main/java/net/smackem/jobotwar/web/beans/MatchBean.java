package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;

public class MatchBean extends EntityBean {
    @JsonProperty private final Collection<MatchRobot> robots = new ArrayList<>();
    @JsonProperty private final Collection<MatchEvent> eventLog = new ArrayList<>();
    @JsonProperty private String gameVersion;
    @JsonProperty private long durationMillis;
    @JsonProperty private int boardWidth;
    @JsonProperty private int boardHeight;
    @JsonProperty private int maxDurationMillis;
    @JsonProperty private String winnerId;
    @JsonProperty private SimulationResult.Outcome outcome;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime dateStarted;

    @JsonCreator
    private MatchBean() {
        this.gameVersion = null;
    }

    public MatchBean(String id) {
        super(id);
        this.gameVersion = Constants.GAME_VERSION;
    }

    public String gameVersion() {
        return this.gameVersion;
    }

    public MatchBean gameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
        return this;
    }

    public int boardWidth() {
        return this.boardWidth;
    }

    public MatchBean boardWidth(int boardWidth) {
        assertMutable();
        this.boardWidth = boardWidth;
        return this;
    }

    public int boardHeight() {
        return this.boardHeight;
    }

    public MatchBean boardHeight(int boardHeight) {
        assertMutable();
        this.boardHeight = boardHeight;
        return this;
    }

    public Duration maxDuration() {
        return Duration.ofMillis(this.maxDurationMillis);
    }

    public MatchBean maxDuration(Duration maxDurationMillis) {
        assertMutable();
        this.maxDurationMillis = (int) maxDurationMillis.toMillis();
        return this;
    }

    public SimulationResult.Outcome outcome() {
        return this.outcome;
    }

    public MatchBean outcome(SimulationResult.Outcome outcome) {
        assertMutable();
        this.outcome = outcome;
        return this;
    }

    public String winnerId() {
        return this.winnerId;
    }

    public MatchBean winnerId(String winnerId) {
        assertMutable();
        this.winnerId = winnerId;
        return this;
    }

    public Duration duration() {
        return Duration.ofMillis(this.durationMillis);
    }

    public MatchBean duration(Duration duration) {
        assertMutable();
        this.durationMillis = duration.toMillis();
        return this;
    }

    public Collection<MatchEvent> eventLog() {
        return Collections.unmodifiableCollection(this.eventLog);
    }

    public MatchBean addEvents(MatchEvent... events) {
        assertMutable();
        this.eventLog.addAll(List.of(events));
        return this;
    }

    public OffsetDateTime dateStarted() {
        return this.dateStarted;
    }

    public MatchBean dateStarted(OffsetDateTime dateStarted) {
        assertMutable();
        this.dateStarted = dateStarted;
        return this;
    }

    public Collection<MatchRobot> robots() {
        return Collections.unmodifiableCollection(this.robots);
    }

    public MatchBean addRobots(MatchRobot... robots) {
        assertMutable();
        this.robots.addAll(List.of(robots));
        return this;
    }

    @Override
    public <T extends FreezableBean> T freeze() {
        for (final MatchRobot matchRobot : this.robots) {
            matchRobot.freeze();
        }
        return super.freeze();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final MatchBean matchBean = (MatchBean) o;
        return durationMillis == matchBean.durationMillis &&
               boardWidth == matchBean.boardWidth &&
               boardHeight == matchBean.boardHeight &&
               maxDurationMillis == matchBean.maxDurationMillis &&
               robots.equals(matchBean.robots) &&
               eventLog.equals(matchBean.eventLog) &&
               Objects.equals(dateStarted, matchBean.dateStarted) &&
               Objects.equals(winnerId, matchBean.winnerId) &&
               outcome == matchBean.outcome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), robots, eventLog, durationMillis, dateStarted, boardWidth, boardHeight, maxDurationMillis, winnerId, outcome);
    }

    @Override
    public String toString() {
        return "MatchBean{" +
               "robotIds=" + robots +
               ", eventLog=" + eventLog +
               ", durationMillis=" + durationMillis +
               ", boardWidth=" + boardWidth +
               ", boardHeight=" + boardHeight +
               ", maxDurationMillis=" + maxDurationMillis +
               ", winnerId='" + winnerId + '\'' +
               ", outcome=" + outcome +
               ", dateStarted=" + dateStarted +
               '}';
    }
}
