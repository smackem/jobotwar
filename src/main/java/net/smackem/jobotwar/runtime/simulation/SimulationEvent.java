package net.smackem.jobotwar.runtime.simulation;

import java.time.Duration;
import java.util.Objects;

public class SimulationEvent {
    private final long gameTimeMillis;
    private final String event;

    SimulationEvent(long gameTimeMillis, String event) {
        this.gameTimeMillis = gameTimeMillis;
        this.event = event;
    }

    public Duration gameTime() {
        return Duration.ofMillis(this.gameTimeMillis);
    }

    public String event() {
        return this.event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SimulationEvent that = (SimulationEvent) o;
        return gameTimeMillis == that.gameTimeMillis
                && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameTimeMillis, event);
    }
}
