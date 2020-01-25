package net.smackem.jobotwar.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.SimulationRunner;

import java.time.Duration;

public class SimulationController {

    private final ObservableList<MatchViewModel> matches = FXCollections.observableArrayList();

    @FXML
    private TableView<MatchViewModel> matchTable;
    @FXML
    private TableColumn<MatchViewModel, String> winnerColumn;
    @FXML
    private TableColumn<MatchViewModel, Duration> durationColumn;

    @FXML
    private void initialize() {
        this.winnerColumn.setCellValueFactory(cell -> cell.getValue().winnerName);
        this.durationColumn.setCellValueFactory(cell -> cell.getValue().duration);
        this.matchTable.setItems(this.matches);
    }

    @FXML
    private void startSimulation(ActionEvent actionEvent) {
        for (int i = 0; i < 1000; i++) {
            final Board board = App.instance().copyBoard();
            final SimulationRunner runner = new SimulationRunner(board);
            final SimulationRunner.SimulationResult result = runner.runGame(Duration.ofMinutes(5));
            final MatchViewModel match = new MatchViewModel(result);
            this.matches.add(match);
        }
    }

    private static class MatchViewModel {
        private final StringProperty winnerName = new SimpleStringProperty();
        private final ObjectProperty<Duration> duration = new SimpleObjectProperty<>();

        private MatchViewModel(SimulationRunner.SimulationResult result) {
            this.winnerName.set(result.winner() != null ? result.winner().name() : "-");
            this.duration.set(result.duration());
        }
    }
}
