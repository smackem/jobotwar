package net.smackem.jobotwar.gui.main;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.gui.graphics.RgbConvert;
import net.smackem.jobotwar.runtime.Robot;

import java.util.Objects;

class MainRobotViewModel {
    private final Robot robot;
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final IntegerProperty health = new SimpleIntegerProperty();
    private final DoubleProperty speedX = new SimpleDoubleProperty();
    private final DoubleProperty speedY = new SimpleDoubleProperty();

    public MainRobotViewModel(Robot robot) {
        this.robot = Objects.requireNonNull(robot);
        update();
    }

    public ReadOnlyStringProperty nameProperty() {
        return this.name;
    }

    public ReadOnlyObjectProperty<Color> colorProperty() {
        return this.color;
    }

    public ReadOnlyIntegerProperty healthProperty() {
        return this.health;
    }

    public ReadOnlyDoubleProperty speedXProperty() {
        return this.speedX;
    }

    public ReadOnlyDoubleProperty speedYProperty() {
        return this.speedY;
    }

    public void update() {
        this.name.set(this.robot.name());
        this.color.set(RgbConvert.toColor(this.robot.rgb()));
        this.health.set(this.robot.getHealth());
        this.speedX.set(this.robot.getSpeedX());
        this.speedY.set(this.robot.getSpeedY());
    }
}
