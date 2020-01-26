package net.smackem.jobotwar.gui;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.SimulationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);
    private final ObservableList<MatchViewModel> matches = FXCollections.observableArrayList();
    private final BooleanProperty running = new SimpleBooleanProperty();

    @FXML
    private TableView<MatchViewModel> matchTable;
    @FXML
    private TableColumn<MatchViewModel, String> winnerColumn;
    @FXML
    private TableColumn<MatchViewModel, Duration> durationColumn;
    @FXML
    private Pane runningOverlay;
    @FXML
    private Spinner<Integer> matchCountSpinner;

    @FXML
    private void initialize() {
        this.winnerColumn.setCellValueFactory(cell -> cell.getValue().winnerName);
        this.winnerColumn.setCellFactory(cell -> new MatchWinnerCell());
        this.durationColumn.setCellValueFactory(cell -> cell.getValue().duration);
        this.matchTable.setItems(this.matches);
        this.runningOverlay.visibleProperty().bind(this.running);
        this.matchCountSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(100, 1000, 10_000, 100_000)
                ));
    }

    @FXML
    private void startSimulation(ActionEvent actionEvent) {
        this.matches.clear();
        this.running.set(true);
        final long start = System.currentTimeMillis();
        runMatches(matchCountSpinner.getValue()).thenAcceptAsync(matches -> {
            this.matches.addAll(matches);
            log.info("Serial: Elapsed milliseconds: {}", System.currentTimeMillis() - start);
            this.running.set(false);
        }, PlatformExecutor.INSTANCE);
    }

    private CompletableFuture<Collection<MatchViewModel>> runMatches(int count) {
        final App app = App.instance();
        final Duration duration = Duration.ofMinutes(5);
        return CompletableFuture.supplyAsync(() -> Stream.generate(app::copyBoard)
                .limit(count)
                .parallel()
                .map(board -> {
                    final SimulationRunner runner = new SimulationRunner(board);
                    final SimulationRunner.SimulationResult result = runner.runGame(duration);
                    return new MatchViewModel(result);
                })
                .collect(Collectors.toList()));
    }

    private static class MatchViewModel {
        private final StringProperty winnerName = new SimpleStringProperty();
        private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>();
        private final Paint winnerPaint;

        private MatchViewModel(SimulationRunner.SimulationResult result) {
            final Robot winner = result.winner();
            if (winner != null) {
                this.winnerName.set(winner.name());
                this.winnerPaint = RgbConvert.toColor(result.winner().rgb());
            } else {
                this.winnerName.set("-");
                this.winnerPaint = null;
            }
            this.duration.set(result.duration());
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
}
