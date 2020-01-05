package net.smackem.jobotwar.gui;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController {

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
    private Label winnerOverlay;

    public MainController() {
        final Board board = App.instance().board();
        this.robots = FXCollections.observableArrayList(board.robots().stream()
                        .map(MainRobotViewModel::new)
                        .collect(Collectors.toList()));
        this.ticker = new Timeline(new KeyFrame(Duration.millis(40), this::tick));
        this.ticker.setCycleCount(Animation.INDEFINITE);
        this.engine = new GameEngine(board);
        this.graphics = new BoardGraphics(board);
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

    private void tick(ActionEvent actionEvent) {
        final GameEngine.TickResult tickResult = this.engine.tick();
        updateGraphics(tickResult);
        this.graphics.render(this.canvas.getGraphicsContext2D());
        for (final MainRobotViewModel r : this.robots) {
            r.update();
        }
        if (tickResult.winner != null) {
            this.winnerOverlay.setText(tickResult.winner.name() + " has won!");
            this.winnerOverlay.setTextFill(RgbConvert.toColor(tickResult.winner.rgb()));
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
    }

    private Parent createRobotGauge(MainRobotViewModel robot) {
        final Label nameLabel = new Label();
        nameLabel.textProperty().bind(robot.nameProperty());
        nameLabel.textFillProperty().set(robot.colorProperty().get());
        final Label healthLabel = new Label();
        healthLabel.textProperty().bind(robot.healthProperty().asString().concat("%"));
        final Label speedXLabel = new Label();
        speedXLabel.textProperty().bind(Bindings.format("Speed X: %f", robot.speedXProperty()));
        final Label speedYLabel = new Label();
        speedYLabel.textProperty().bind(Bindings.format("Speed Y: %f", robot.speedYProperty()));
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

    private Collection<Robot> createRobots() {
        final Robot r1 = new Robot.Builder(
            robot -> new RuntimeProgram(robot,
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(4); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedY(3); return null;
                }),
                RuntimeProgram.instruction("MOVE", r ->
                    r.getX() > 150 ? null : "MOVE"
                ),
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedY(0); return null;
                }),
                RuntimeProgram.instruction("SHOOT", r -> {
                    r.setAimAngle((r.getAimAngle() + 5) % 360); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(500); return null;
                }),
                RuntimeProgram.instruction(null, r -> "SHOOT")
        )).rgb(0xff0000).x(20).y(50).build();

        final double[] radarAngle2 = new double[] { 0 };
        final Robot r2 = new Robot.Builder(
            robot -> new RuntimeProgram(robot,
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(-0.5); return null;
                }),
                RuntimeProgram.instruction("MOVE", r -> {
                    r.setRadarAngle(radarAngle2[0] % 360);
                    radarAngle2[0] += 12;
                    return null;
                }),
                RuntimeProgram.instruction(null, r ->
                    r.getX() > 10 ? "MOVE" : null
                )
        )).rgb(0xffc020).x(500).y(400).build();

        final double[] radarAngle3 = new double[] { 0 };
        final Robot r3 = new Robot.Builder(
            robot -> new RuntimeProgram(robot,
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(0.1); return null;
                }),
                RuntimeProgram.instruction("RADAR", r -> {
                    r.setRadarAngle(radarAngle3[0] % 360);
                    radarAngle3[0] += 5;
                    return "RADAR";
                })
        )).rgb(0x0040ff).x(20).y(400).build();

        final double[] radarAngle4 = new double[] { 0 };
        final Robot r4 = new Robot.Builder(
            robot -> new RuntimeProgram(robot,
                RuntimeProgram.instruction(null, r -> {
                    r.setAimAngle(90); return null;
                }),
                RuntimeProgram.instruction("LEFT", r -> {
                    r.setSpeedX(-5); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(500); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    return r.getX() > 100 ? "LEFT" : null;
                }),
                RuntimeProgram.instruction("RIGHT", r -> {
                    r.setSpeedX(5); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(500); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    return r.getX() < 500 ? "RIGHT" : null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    return "LEFT";
                })
        )).rgb(0xC02000).x(600).y(30).build();

        return Arrays.asList(r1, r2, r3, r4);
    }
}
