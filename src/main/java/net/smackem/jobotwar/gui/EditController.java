package net.smackem.jobotwar.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class EditController {

    private final ObservableList<RobotViewModel> robots = FXCollections.observableArrayList();

    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea sourceText;
    @FXML
    private ListView<RobotViewModel> robotsListView;
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private void initialize() {
        this.robotsListView.setItems(this.robots);
        this.robotsListView.getSelectionModel().selectedItemProperty().addListener((prop, old, val) -> {
        });
    }

    @FXML
    private void startGame(MouseEvent mouseEvent) throws IOException {
        App.instance().setRoot("main");
    }

    @FXML
    private void newRobot(MouseEvent mouseEvent) {
        final RobotViewModel robot = new RobotViewModel();
        robot.nameProperty().set("Robot " + this.robots.size() + 1);
        this.robots.add(robot);
    }

    @FXML
    private void removeRobot(MouseEvent mouseEvent) {
        final RobotViewModel selectedRobot = this.robotsListView.getSelectionModel().getSelectedItem();
        if (selectedRobot != null) {
            this.robots.remove(selectedRobot);
        }
    }
}
