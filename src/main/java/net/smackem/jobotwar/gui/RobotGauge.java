package net.smackem.jobotwar.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.smackem.jobotwar.runtime.Constants;

import java.io.IOException;

public class RobotGauge extends VBox {

    private final MainRobotViewModel robot;

    @FXML
    private Label nameLabel;
    @FXML
    private Label healthLabel;
    @FXML
    private Label speedXLabel;
    @FXML
    private Label speedYLabel;

    public RobotGauge(MainRobotViewModel robot) {
        this.robot = robot;
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("robotgauge.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            assert(false);
        }
    }

    @FXML
    private void initialize() {
        this.nameLabel.textProperty().bind(this.robot.nameProperty());
        this.nameLabel.textFillProperty().set(this.robot.colorProperty().get());
        this.healthLabel.textProperty().bind(this.robot.healthProperty().asString().concat("%"));
        this.speedXLabel.textProperty().bind(Bindings.format(
                "Speed X: %.1f",
                this.robot.speedXProperty().multiply(Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED)));
        this.speedYLabel.textProperty().bind(Bindings.format(
                "Speed Y: %.1f",
                this.robot.speedYProperty().multiply(Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED)));
    }
}
