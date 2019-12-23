package net.smackem.jobotwar.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.GameEngine;
import net.smackem.jobotwar.runtime.LoopProgram;
import net.smackem.jobotwar.runtime.Robot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainController {

    private final Timeline ticker;
    private final BoardGraphics graphics;
    private final Board board;
    private final GameEngine engine;

    @FXML
    private StackPane canvasContainer;
    @FXML
    private Canvas canvas;

    public MainController() {
        this.ticker = new Timeline(
                new KeyFrame(Duration.millis(40), this::tick));
        this.ticker.setCycleCount(Animation.INDEFINITE);
        this.board = new Board(500, 500, createRobots());
        this.engine = new GameEngine(this.board);
        this.graphics = new BoardGraphics(this.board);
    }

    @FXML
    private void initialize() {
        this.canvasContainer.maxWidthProperty().bind(this.canvas.widthProperty());
        this.canvasContainer.maxHeightProperty().bind(this.canvas.heightProperty());
        this.canvas.setWidth(this.board.getWidth());
        this.canvas.setHeight(this.board.getHeight());
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
        this.engine.tick();
        final GraphicsContext gc = this.canvas.getGraphicsContext2D();
        this.graphics.render(gc);
    }

    private Collection<Robot> createRobots() {
        final Robot r1 = new Robot(0.1, new LoopProgram(
                r -> {
                    if (r.getX() > 150) {
                        r.setAimAngle((r.getAimAngle() + 15) % 360);
                        r.setShot(200);
                        r.setSpeedX(0);
                        r.setSpeedY(0);
                        return;
                    }
                    r.setSpeedX(4);
                    r.setSpeedY(3);
                }
        ));
        r1.setX(10);
        r1.setY(50);
        return Collections.singleton(r1);
    }
}
