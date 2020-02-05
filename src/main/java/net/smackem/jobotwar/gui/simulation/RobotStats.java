package net.smackem.jobotwar.gui.simulation;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;

import java.io.IOException;

class RobotStats extends HBox {

    private final RobotStatisticsViewModel stats;

    @FXML
    private Ellipse paintElement;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ratioLabel;

    public RobotStats(RobotStatisticsViewModel stats) {
        this.stats = stats;
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("robotstats.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            assert false;
        }
    }

    @FXML
    private void initialize() {
        this.nameLabel.setText(this.stats.robotName() + ":");
        this.ratioLabel.textProperty().bind(Bindings.format("%.2f %%", this.stats.winRatioProperty()));

        final Paint paint = this.stats.robotPaint();
        if (paint != null) {
            this.paintElement.setFill(paint);
        }
    }
}
