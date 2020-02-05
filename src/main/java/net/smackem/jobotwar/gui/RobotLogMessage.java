package net.smackem.jobotwar.gui;

import net.smackem.jobotwar.runtime.Robot;

public class RobotLogMessage {
    private final Robot robot;
    private final String category;
    private final Double value;

    public RobotLogMessage(Robot robot, String category, Double value) {
        this.robot = robot;
        this.category = category;
        this.value = value;
    }

    public Robot robot() {
        return robot;
    }

    public String category() {
        return category;
    }

    public Double value() {
        return value;
    }
}
