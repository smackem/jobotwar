package net.smackem.jobotwar.gui.main;

import com.google.common.eventbus.Subscribe;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.smackem.jobotwar.gui.App;
import net.smackem.jobotwar.gui.graphics.BoardGraphics;
import net.smackem.jobotwar.gui.graphics.RgbConvert;
import net.smackem.jobotwar.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private final Timeline ticker;
    private final BoardGraphics graphics;
    private final GameEngine engine;
    private final ObservableList<MainRobotViewModel> robots;
    private final BooleanProperty replay = new SimpleBooleanProperty();

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
    @FXML
    private Label replayLabel;

    public MainController() {
        final App app = App.instance();
        final Board board = app.board();
        this.robots = FXCollections.observableArrayList(board.robots().stream()
                        .map(MainRobotViewModel::new)
                        .collect(Collectors.toList()));
        this.ticker = new Timeline(new KeyFrame(Duration.millis(Constants.TICK_DURATION.toMillis()), this::tick));
        this.ticker.setCycleCount(Animation.INDEFINITE);
        this.engine = new GameEngine(board);
        this.graphics = new BoardGraphics(board);
        this.replay.set(app.isReplay());
        app.eventBus().register(this);
    }

    @FXML
    private void initialize() {
        final Board board = this.engine.board();
        this.canvasContainer.maxWidthProperty().bind(this.canvas.widthProperty());
        this.canvasContainer.maxHeightProperty().bind(this.canvas.heightProperty());
        this.canvas.setWidth(board.width());
        this.canvas.setHeight(board.height());
        this.graphics.render(this.canvas.getGraphicsContext2D());
        this.messagesTextArea.appendText("Welcome to the battle of programs!\n");
        this.replayLabel.visibleProperty().bind(this.replay);

        for (final MainRobotViewModel robot : this.robots) {
            robotGaugesParent.getChildren().add(createRobotGauge(robot));
        }
    }

    @FXML
    private void startGame(MouseEvent mouseEvent) {
        this.ticker.play();
        this.messagesTextArea.appendText("Game started at " + LocalDateTime.now() + "\n");
    }

    @FXML
    private void stopGame(MouseEvent mouseEvent) {
        this.ticker.stop();
        this.messagesTextArea.appendText("Game paused at " + LocalDateTime.now() + "\n");
    }

    @FXML
    private void newGame(ActionEvent actionEvent) throws IOException {
        this.ticker.stop();
        final App app = App.instance();
        app.eventBus().unregister(this);
        app.showEditor();
    }

    private void tick(ActionEvent actionEvent) {
        final GameEngine.TickResult tickResult;
        try {
            tickResult = this.engine.tick();
        } catch (RobotProgramException e) {
            this.ticker.stop();
            this.messagesTextArea.appendText(String.format("ERROR @ Robot '%s': %s", e.robotName(), e.getMessage()));
            return;
        }

        final GraphicsContext gc = this.canvas.getGraphicsContext2D();
        updateGraphics(tickResult);
        this.graphics.render(gc);

        for (final MainRobotViewModel r : this.robots) {
            r.update();
        }
        for (final Robot robot : tickResult.killedRobots()) {
            this.messagesTextArea.appendText(robot.name() + " got killed.\n");
        }
        if (tickResult.hasEnded()) {
            showGameResult(tickResult);
        }
    }

    private void showGameResult(GameEngine.TickResult tickResult) {
        final Robot winner = tickResult.winner();
        if (winner != null) {
            this.winnerLabel.setText(winner.name() + " has won!");
            this.winnerLabel.setTextFill(RgbConvert.toColor(winner.rgb()));
            this.winnerOverlay.setVisible(true);
            this.messagesTextArea.appendText(winner.name() + " has won the game!\n");
            return;
        }
        if (tickResult.isDraw()) {
            this.winnerLabel.setText("Draw!");
            this.winnerLabel.setTextFill(Color.WHITE);
            this.winnerOverlay.setVisible(true);
            this.messagesTextArea.appendText("The game has ended with a draw!\n");
        }
    }

    private void updateGraphics(GameEngine.TickResult tickResult) {
        this.graphics.addExplosions(Stream.concat(
                tickResult.explodedProjectiles().stream().map(Projectile::position),
                Stream.concat(
                        tickResult.collisionPositions().stream(),
                        tickResult.killedRobots().stream().map(r -> new Vector(r.getX(), r.getY())))
        ).collect(Collectors.toList()));
        this.graphics.addRadarBeams(tickResult.radarBeams());
        if (tickResult.winner() != null) {
            this.graphics.createWinnerAnimation();
        }
    }

    private Parent createRobotGauge(MainRobotViewModel robot) {
        final RobotGauge robotGauge = new RobotGauge(robot);
        robotGauge.getStyleClass().add("robotGauge");
        robotGauge.opacityProperty().bind(
                Bindings.when(robot.healthProperty().greaterThan(0.0))
                        .then(1.0)
                        .otherwise(0.3));
        return robotGauge;
    }

    @Subscribe
    private void onRobotLogMessage(RobotLogMessage message) {
        this.messagesTextArea.appendText(String.format("[%s] %s: %f\n",
                message.robotName(), message.category(), message.value()));
    }
}
