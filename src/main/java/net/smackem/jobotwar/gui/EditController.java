package net.smackem.jobotwar.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.Robot;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class EditController {

    private final ObservableList<RobotViewModel> robots = FXCollections.observableArrayList();
    private final ObjectProperty<RobotViewModel> selectedRobot = new SimpleObjectProperty<>();

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
            selectRobot(val);
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
        final RobotViewModel robot = this.selectedRobot.get();
        if (robot != null) {
            this.robots.remove(robot);
        }
    }

    private void selectRobot(RobotViewModel robot) {
        final RobotViewModel oldRobot = this.selectedRobot.get();
        if (oldRobot != null) {
            this.nameTextField.textProperty().unbindBidirectional(oldRobot.nameProperty());
            this.colorPicker.valueProperty().unbindBidirectional(oldRobot.colorProperty());
            this.sourceText.textProperty().unbindBidirectional(oldRobot.sourceCodeProperty());
        }
        this.selectedRobot.set(robot);
        if (robot != null) {
            this.nameTextField.textProperty().bindBidirectional(robot.nameProperty());
            this.colorPicker.valueProperty().bindBidirectional(robot.colorProperty());
            this.sourceText.textProperty().bindBidirectional(robot.sourceCodeProperty());
        }
    }

    private Collection<Robot> createRobotsFromViewModel() {
        return this.robots.stream()
                .map(this::createRobotFromViewModel)
                .collect(Collectors.toList());
    }

    private Robot createRobotFromViewModel(RobotViewModel r) {
        final Compiler compiler = new Compiler();
        final Program program = compiler.compile("");
        final Robot robot = new Robot(0.5, 0xff, 10, null);
        return robot;
    }
}
