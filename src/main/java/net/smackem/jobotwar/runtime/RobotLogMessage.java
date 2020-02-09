package net.smackem.jobotwar.runtime;

public class RobotLogMessage {
    private final String robotName;
    private final String category;
    private final Double value;

    public RobotLogMessage(String robotName, String category, Double value) {
        this.robotName = robotName;
        this.category = category;
        this.value = value;
    }

    public String robotName() {
        return this.robotName;
    }

    public String category() {
        return this.category;
    }

    public Double value() {
        return this.value;
    }
}
