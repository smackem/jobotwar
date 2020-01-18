package net.smackem.jobotwar.gui;

import net.smackem.jobotwar.runtime.Robot;

public class RobotLogMessage {
    final Robot robot;
    final String category;
    final Double value;

    public RobotLogMessage(Robot robot, String category, Double value) {
        this.robot = robot;
        this.category = category;
        this.value = value;
    }
}
