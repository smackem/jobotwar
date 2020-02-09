package net.smackem.jobotwar.gui.simulation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Paint;

class RobotStatisticsViewModel {
    private final String robotName;
    private final Paint robotPaint;
    private final DoubleProperty winRatio = new SimpleDoubleProperty();
    int winCount;

    public RobotStatisticsViewModel(String robotName, Paint robotPaint) {
        this.robotName = robotName;
        this.robotPaint = robotPaint;
    }

    public String robotName() {
        return this.robotName;
    }

    public Paint robotPaint() {
        return this.robotPaint;
    }

    public DoubleProperty winRatioProperty() {
        return this.winRatio;
    }
}
