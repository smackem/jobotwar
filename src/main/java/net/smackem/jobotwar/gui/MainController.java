package net.smackem.jobotwar.gui;

import com.google.common.eventbus.Subscribe;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Interpreter;
import net.smackem.jobotwar.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage") // for EventBus
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private final Timeline ticker;
    private final BoardGraphics graphics;
    private final GameEngine engine;
    private final ObservableList<MainRobotViewModel> robots;

    @FXML
    private StackPane canvasContainer;
    @FXML
    private Canvas canvas;
    @FXML
    private Pane robotGaugesParent;
    @FXML
    private Pane winnerOverlay;
    @FXML
    private Label winnerLabel;
    @FXML
    private TextArea messagesTextArea;

    public MainController() {
        final Board board = App.instance().board();
        this.robots = FXCollections.observableArrayList(board.robots().stream()
                        .map(MainRobotViewModel::new)
                        .collect(Collectors.toList()));
        this.ticker = new Timeline(new KeyFrame(Duration.millis(40), this::tick));
        this.ticker.setCycleCount(Animation.INDEFINITE);
        this.engine = new GameEngine(board);
        this.graphics = new BoardGraphics(board);
        App.instance().eventBus().register(this);
    }

    @FXML
    private void initialize() {
        final Board board = App.instance().board();
        this.canvasContainer.maxWidthProperty().bind(this.canvas.widthProperty());
        this.canvasContainer.maxHeightProperty().bind(this.canvas.heightProperty());
        this.canvas.setWidth(board.width());
        this.canvas.setHeight(board.height());
        this.graphics.render(this.canvas.getGraphicsContext2D());

        for (final MainRobotViewModel robot : this.robots) {
            robotGaugesParent.getChildren().add(createRobotGauge(robot));
        }
    }

    @FXML
    private void startGame(MouseEvent mouseEvent) {
        this.ticker.play();
    }

    @FXML
    private void stopGame(MouseEvent mouseEvent) {
        this.ticker.stop();
    }

    @FXML
    private void newGame(ActionEvent actionEvent) throws IOException {
        this.ticker.stop();
        App.instance().eventBus().unregister(this);
        App.instance().showEditor();
    }

    private void tick(ActionEvent actionEvent) {
        final GameEngine.TickResult tickResult = this.engine.tick();
        final GraphicsContext gc = this.canvas.getGraphicsContext2D();
        updateGraphics(tickResult);
        this.graphics.render(gc);
        for (final MainRobotViewModel r : this.robots) {
            r.update();
        }
        if (tickResult.winner != null) {
            this.winnerLabel.setText(tickResult.winner.name() + " has won!");
            this.winnerLabel.setTextFill(RgbConvert.toColor(tickResult.winner.rgb()));
            this.winnerOverlay.setVisible(true);
        }
    }

    private void updateGraphics(GameEngine.TickResult tickResult) {
        this.graphics.addExplosions(Stream.concat(
                tickResult.explodedProjectiles.stream().map(Projectile::position),
                Stream.concat(
                        tickResult.collisionPositions.stream(),
                        tickResult.killedRobots.stream().map(r -> new Vector(r.getX(), r.getY())))
        ).collect(Collectors.toList()));
        this.graphics.addRadarBeams(tickResult.radarBeams);
        if (tickResult.winner != null) {
            this.graphics.createWinnerAnimation();
        }
    }

    private Parent createRobotGauge(MainRobotViewModel robot) {
        final Label nameLabel = new Label();
        nameLabel.textProperty().bind(robot.nameProperty());
        nameLabel.textFillProperty().set(robot.colorProperty().get());
        final Label healthLabel = new Label();
        healthLabel.textProperty().bind(robot.healthProperty().asString().concat("%"));
        final Label speedXLabel = new Label();
        speedXLabel.textProperty().bind(Bindings.format(
                "Speed X: %.1f",
                robot.speedXProperty().multiply(Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED)));
        final Label speedYLabel = new Label();
        speedYLabel.textProperty().bind(Bindings.format(
                "Speed Y: %.1f",
                robot.speedYProperty().multiply(Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED)));
        final VBox vbox = new VBox(nameLabel, healthLabel, speedXLabel, speedYLabel);
        vbox.getStyleClass().add("robotGauge");
        vbox.opacityProperty().bind(
                Bindings.when(robot.healthProperty().greaterThan(0.0))
                        .then(1.0)
                        .otherwise(0.3));
        vbox.getChildren().stream()
                .filter(node -> node != nameLabel && node instanceof Label)
                .forEach(node -> node.getStyleClass().add("robotGaugeLabel"));
        return vbox;
    }

    @Subscribe
    private void onRobotLogMessage(RobotLogMessage message) {
        this.messagesTextArea.appendText(String.format("[%s] %s: %f\n", message.robot.name(), message.category, message.value));
    }
}
