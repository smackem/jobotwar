package net.smackem.jobotwar.gui;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.Vector;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class EditController {

    private final ListProperty<RobotViewModel> robots = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<RobotViewModel> selectedRobot = new SimpleObjectProperty<>();
    private final Random random = new Random();
    private static final int BOARD_WIDTH = 640;
    private static final int BOARD_HEIGHT = 480;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextArea sourceText;
    @FXML
    private ListView<RobotViewModel> robotsListView;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button playButton;

    @FXML
    private void initialize() {
        this.robotsListView.setItems(this.robots);
        this.robotsListView.getSelectionModel().selectedItemProperty().addListener((prop, old, val) -> {
            selectRobot(val);
        });
        this.robotsListView.setCellFactory(listView -> new RobotViewModelCell());
        this.playButton.disableProperty().bind(
                this.robots.sizeProperty().lessThanOrEqualTo(0));
        newRobot(null);
    }

    @FXML
    private void startGame(MouseEvent mouseEvent) throws IOException {
        final App app = App.instance();
        app.createBoard(BOARD_WIDTH, BOARD_HEIGHT, createRobotsFromViewModel());
        app.setRoot("main");
    }

    @FXML
    private void newRobot(MouseEvent mouseEvent) {
        final RobotViewModel robot = new RobotViewModel();
        robot.nameProperty().set("Robot " + (this.robots.size() + 1));
        robot.colorProperty().set(Color.hsb(this.random.nextDouble() * 360, 1.0, 1.0));
        robot.sourceCodeProperty().set("");
        this.robots.add(robot);
        this.robotsListView.getSelectionModel().select(robot);
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
        final Collection<Robot> robots = this.robots.stream()
                .map(this::createRobotFromViewModel)
                .collect(Collectors.toList());
        placeRobots(robots);
        return robots;
    }

    private Robot createRobotFromViewModel(RobotViewModel robotViewModel) {
        final Color color = robotViewModel.colorProperty().get();
        final int rgb = (int)(color.getRed() * 0xff) << 16 |
                (int)(color.getGreen() * 0xff) << 8 |
                (int)(color.getBlue() * 0xff);
        return new Robot.Builder(r -> CompiledProgram.compile(r, robotViewModel.sourceCodeProperty().get()))
                .name(robotViewModel.nameProperty().get())
                .rgb(rgb)
                .build();
    }

    private void placeRobots(Collection<Robot> robots) {
        for (final Robot robot : robots) {
            do {
                robot.setX(Constants.ROBOT_RADIUS + this.random.nextDouble() * (BOARD_WIDTH - Constants.ROBOT_RADIUS * 2));
                robot.setY(Constants.ROBOT_RADIUS + this.random.nextDouble() * (BOARD_HEIGHT - Constants.ROBOT_RADIUS * 2));
            } while (getCloseRobot(robots, robot) != null);
        }
    }

    private Robot getCloseRobot(Collection<Robot> robots, Robot test) {
        return robots.stream()
                .filter(r -> r != test)
                .filter(r -> Vector.distance(r.getPosition(), test.getPosition()) < Constants.ROBOT_RADIUS * 2)
                .findFirst()
                .orElse(null);
    }

    private static class RobotViewModelCell extends ListCell<RobotViewModel> {
        @Override
        protected void updateItem(RobotViewModel robotViewModel, boolean empty) {
            super.updateItem(robotViewModel, empty);
            if (empty) {
                textProperty().unbind();
                setText(null);
            } else {
                textProperty().bind(robotViewModel.nameProperty());
            }
        }
    }
}