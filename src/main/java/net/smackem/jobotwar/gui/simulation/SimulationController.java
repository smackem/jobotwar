package net.smackem.jobotwar.gui.simulation;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smackem.jobotwar.gui.App;
import net.smackem.jobotwar.gui.PlatformExecutor;
import net.smackem.jobotwar.gui.RgbConvert;
import net.smackem.jobotwar.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);
    private final ObservableList<MatchViewModel> matches = FXCollections.observableArrayList();
    private final BooleanProperty running = new SimpleBooleanProperty();
    private final Random random = new Random();

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
        return SimulationRunner.runBatchParallel(app.board(), count, this.random, duration)
                .thenApply(batchResults ->
                        batchResults.stream()
                                .map(br -> new MatchViewModel(br, br.matchNumber(), br.recorder()))
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
    private void newGame(ActionEvent actionEvent) throws IOException {
        App.instance().showEditor();
    }

    @FXML
    private void replay(ActionEvent actionEvent) {
        final MatchViewModel match = this.matchTable.getSelectionModel().getSelectedItem();
        if (match == null) {
            return;
        }
        final Board replayBoard = match.recorder.replay();
        App.instance().startReplay(replayBoard);
    }

    private static class MatchViewModel {
        final IntegerProperty matchNumber = new SimpleIntegerProperty();
        final StringProperty winnerName = new SimpleStringProperty();
        final ObjectProperty<Duration> duration = new SimpleObjectProperty<>();
        final Paint winnerPaint;
        final GameRecorder recorder;

        MatchViewModel(SimulationResult result, int matchNumber, GameRecorder recorder) {
            final Robot winner = result.winner();
            if (winner != null) {
                this.winnerName.set(winner.name());
                this.winnerPaint = RgbConvert.toColor(result.winner().rgb());
            } else {
                this.winnerName.set("-");
                this.winnerPaint = null;
            }
            this.matchNumber.set(matchNumber);
            this.duration.set(result.duration());
            this.recorder = recorder;
        }
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
            setText(String.format("%02d:%02d.%03d", item.toMinutes(), item.getSeconds() % 60, item.toMillis() % 1000));
        }
    }
}
