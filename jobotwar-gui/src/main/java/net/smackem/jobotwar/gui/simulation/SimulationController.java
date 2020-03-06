package net.smackem.jobotwar.gui.simulation;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smackem.jobotwar.gui.App;
import net.smackem.jobotwar.gui.PlatformExecutor;
import net.smackem.jobotwar.gui.graphics.BoardGraphics;
import net.smackem.jobotwar.gui.graphics.RgbConvert;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.GameRecorder;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.simulation.BatchSimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);
    private final ObservableList<MatchViewModel> matches = FXCollections.observableArrayList();
    private final BooleanProperty running = new SimpleBooleanProperty();

    @FXML
    private TableView<MatchViewModel> matchTable;
    @FXML
    private TableColumn<MatchViewModel, Integer> numberColumn;
    @FXML
    private TableColumn<MatchViewModel, String> winnerColumn;
    @FXML
    private TableColumn<MatchViewModel, Duration> durationColumn;
    @FXML
    private Pane runningOverlay;
    @FXML
    private ChoiceBox<Integer> matchCountChoice;
    @FXML
    private Pane robotStatsParent;
    @FXML
    private Pane detailsPane;
    @FXML
    private Label outcomeLabel;
    @FXML
    private Label winnerLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Canvas boardCanvas;
    @FXML
    private TextArea eventArea;

    @FXML
    private void initialize() {
        this.numberColumn.setCellValueFactory(cell -> cell.getValue().matchNumber.asObject());
        this.winnerColumn.setCellValueFactory(cell -> cell.getValue().winnerName);
        this.winnerColumn.setCellFactory(cell -> new MatchWinnerCell());
        this.durationColumn.setCellValueFactory(cell -> cell.getValue().duration);
        this.durationColumn.setCellFactory(cell -> new MatchDurationCell());
        this.matchTable.setItems(this.matches);
        this.runningOverlay.visibleProperty().bind(this.running);
        this.matchCountChoice.setItems(FXCollections.observableArrayList(100, 1000, 10_000, 100_000));
        this.matchCountChoice.setValue(1000);
        this.detailsPane.visibleProperty().bind(
                this.matchTable.getSelectionModel().selectedItemProperty().isNotNull());
        this.matchTable.getSelectionModel().selectedItemProperty().addListener(
                (prop, old, val) -> selectMatch(val));
    }

    private void selectMatch(MatchViewModel match) {
        this.outcomeLabel.setText(String.valueOf(match.outcome));
        this.winnerLabel.setText(match.winnerName.get());
        this.durationLabel.setText(formatDuration(match.duration.get()));
        drawBoard(match.recorder.replayBoard());
        this.eventArea.setText(renderEventLogText(match.eventLog));
    }

    private static String renderEventLogText(Collection<SimulationEvent> eventLog) {
        final StringBuilder sb = new StringBuilder();
        for (final SimulationEvent event : eventLog) {
            sb.append(String.format("@%s: %s\n", formatDuration(event.gameTime()), event.event()));
        }
        return sb.toString();
    }

    private void drawBoard(Board board) {
        final double scale = 0.5;
        final double width = board.width(), height = board.height();
        this.boardCanvas.setWidth(width * scale);
        this.boardCanvas.setHeight(height * scale);
        final GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
        final BoardGraphics bg = new BoardGraphics(board);
        gc.save();
        gc.scale(scale, scale);
        bg.render(gc);
        gc.restore();
    }

    @FXML
    private void startSimulation(ActionEvent actionEvent) {
        this.matches.clear();
        this.running.set(true);
        final long start = System.currentTimeMillis();
        runMatches(matchCountChoice.getValue()).thenAcceptAsync(matches -> {
            this.matches.addAll(matches);
            log.info("Simulation: Elapsed milliseconds: {}", System.currentTimeMillis() - start);
            final Collection<RobotStatisticsViewModel> stats = buildRobotStatistics(matches);
            createRobotStatsWidgets(stats);
            this.running.set(false);
        }, PlatformExecutor.INSTANCE);
    }

    private CompletableFuture<Collection<MatchViewModel>> runMatches(int count) {
        final App app = App.instance();
        final Duration duration = Duration.ofMinutes(5);
        return CompletableFuture.supplyAsync(() ->
                SimulationRunner.runBatchParallel(app.board(), count, duration))
                .thenApply(batchResults ->
                        batchResults.stream()
                                .map(MatchViewModel::new)
                                .collect(Collectors.toList()));
    }

    private void createRobotStatsWidgets(Collection<RobotStatisticsViewModel> stats) {
        this.robotStatsParent.getChildren().clear();
        for (final RobotStatisticsViewModel stat : stats) {
            final RobotStats node = new RobotStats(stat);
            this.robotStatsParent.getChildren().add(node);
            log.info("{}: {} ({})", stat.robotName(), stat.winRatioProperty().get(), stat.winCount);
        }
    }

    private Collection<RobotStatisticsViewModel> buildRobotStatistics(Collection<MatchViewModel> matches) {
        final Map<String, RobotStatisticsViewModel> stats = new HashMap<>();
        for (final MatchViewModel match : matches) {
            RobotStatisticsViewModel stat = stats.get(match.winnerName.get());
            if (stat == null) {
                stat = new RobotStatisticsViewModel(match.winnerName.get(), match.winnerPaint);
                stats.put(match.winnerName.get(), stat);
            }
            stat.winCount++;
        }
        for (final RobotStatisticsViewModel stat : stats.values()) {
            stat.winRatioProperty().set((double)stat.winCount / (double)matches.size());
        }
        return stats.values().stream()
                .sorted(Comparator.comparingDouble(stat -> -stat.winRatioProperty().get()))
                .collect(Collectors.toList());
    }

    @FXML
    private void newGame(ActionEvent actionEvent) {
        App.instance().showEditor();
    }

    @FXML
    private void replay(ActionEvent actionEvent) {
        final MatchViewModel match = this.matchTable.getSelectionModel().getSelectedItem();
        if (match == null) {
            return;
        }
        final Board replayBoard = match.recorder.replay(msg -> App.instance().eventBus().post(msg));
        App.instance().startReplay(replayBoard);
    }

    private static class MatchViewModel {
        final IntegerProperty matchNumber = new SimpleIntegerProperty();
        final StringProperty winnerName = new SimpleStringProperty();
        final ObjectProperty<Duration> duration = new SimpleObjectProperty<>();
        final Paint winnerPaint;
        final GameRecorder recorder;
        final SimulationResult.Outcome outcome;
        final Collection<SimulationEvent> eventLog;

        MatchViewModel(BatchSimulationResult result) {
            final Robot winner = result.winner();
            if (winner != null) {
                this.winnerName.set(winner.name());
                this.winnerPaint = RgbConvert.toColor(result.winner().rgb());
            } else {
                this.winnerName.set("-");
                this.winnerPaint = null;
            }
            this.matchNumber.set(result.matchNumber());
            this.duration.set(result.duration());
            this.recorder = result.recorder();
            this.outcome = result.outcome();
            this.eventLog = result.eventLog();
        }
    }

    private static String formatDuration(Duration d) {
        return String.format("%02d:%02d.%03d", d.toMinutes(), d.getSeconds() % 60, d.toMillis() % 1000);
    }

    private static class MatchWinnerCell extends TableCell<MatchViewModel, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                return;
            }
            final MatchViewModel match = getTableView().getItems().get(getIndex());
            setTextFill(match.winnerPaint != null ? match.winnerPaint : Color.BLACK);
            setText(item);
        }
    }

    private static class MatchDurationCell extends TableCell<MatchViewModel, Duration> {
        @Override
        protected void updateItem(Duration item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                return;
            }
            setText(formatDuration(item));
        }
    }
}
