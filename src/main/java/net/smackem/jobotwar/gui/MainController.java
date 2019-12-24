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
import net.smackem.jobotwar.runtime.*;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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
        this.board = new Board(640, 480, createRobots());
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
        final Collection<Projectile> explodedProjectiles = this.engine.tick();
        this.graphics.addExplosions(explodedProjectiles.stream()
                .map(Projectile::getPosition)
                .collect(Collectors.toList()));
        final GraphicsContext gc = this.canvas.getGraphicsContext2D();
        this.graphics.render(gc);
    }

    private Collection<Robot> createRobots() {
        final Robot r1 = new Robot(0.1, 0, new RuntimeProgram(
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
                    r.setShot(200); return null;
                }),
                RuntimeProgram.instruction(null, r -> "SHOOT")
        ));
        r1.setX(10);
        r1.setY(50);
        return Collections.singleton(r1);
    }
}
