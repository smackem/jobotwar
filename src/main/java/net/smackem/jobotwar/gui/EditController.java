package net.smackem.jobotwar.gui;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.Vector;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class EditController {

    private final ListProperty<EditRobotViewModel> robots = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<EditRobotViewModel> selectedRobot = new SimpleObjectProperty<>();
    private final Random random = new Random();
    private static final int BOARD_WIDTH = 640;
    private static final int BOARD_HEIGHT = 480;

    @FXML
    private TextField nameTextField;
    @FXML
    private CodeEditor sourceText;
    @FXML
    private ListView<EditRobotViewModel> robotsListView;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button playButton;
    @FXML
    private TextArea compilerOutput;

    @FXML
    private void initialize() {
        this.robotsListView.setItems(this.robots);
        this.robotsListView.getSelectionModel().selectedItemProperty().addListener((prop, old, val) -> {
            selectRobot(val);
        });
        this.robotsListView.setCellFactory(listView -> new RobotViewModelCell());
        this.playButton.disableProperty().bind(
                this.robots.sizeProperty().lessThanOrEqualTo(0));
        this.sourceText.textProperty().addListener((prop, old, val) -> {
            this.selectedRobot.get().sourceCodeProperty().set(val);
        });
        newRobot(null);
    }

    @FXML
    private void startGame(ActionEvent mouseEvent) throws IOException {
        final App app = App.instance();
        final Collection<Robot> robots = createRobotsFromViewModel();
        if (robots.isEmpty()) {
            return;
        }
        app.createBoard(BOARD_WIDTH, BOARD_HEIGHT, robots);
        app.setRoot("main");
    }

    @FXML
    private void newRobot(ActionEvent mouseEvent) {
        newRobotWithProgram("Robot", null);
    }

    private void newRobotWithProgram(String robotBaseName, String resourceName) {
        final EditRobotViewModel robot = new EditRobotViewModel();
        robot.nameProperty().set(robotBaseName + " " + (this.robots.size() + 1));
        robot.colorProperty().set(Color.hsb(this.random.nextDouble() * 360, 1.0, 1.0));
        if (resourceName != null) {
            try (final InputStream stream = getClass().getResourceAsStream(resourceName);
                 final InputStreamReader reader = new InputStreamReader(stream)) {
                robot.sourceCodeProperty().set(CharStreams.toString(reader));
            } catch (Exception ignored) {
            }
        } else {
            robot.sourceCodeProperty().set("");
        }
        this.robots.add(robot);
        this.robotsListView.getSelectionModel().select(robot);
    }

    @FXML
    private void removeRobot(ActionEvent mouseEvent) {
        final EditRobotViewModel robot = this.selectedRobot.get();
        if (robot != null) {
            this.robots.remove(robot);
        }
    }

    private void selectRobot(EditRobotViewModel robot) {
        final EditRobotViewModel oldRobot = this.selectedRobot.get();
        if (oldRobot != null) {
            this.nameTextField.textProperty().unbindBidirectional(oldRobot.nameProperty());
            this.colorPicker.valueProperty().unbindBidirectional(oldRobot.colorProperty());
        }
        this.selectedRobot.set(robot);
        if (robot != null) {
            this.nameTextField.textProperty().bindBidirectional(robot.nameProperty());
            this.colorPicker.valueProperty().bindBidirectional(robot.colorProperty());
            this.sourceText.replaceText(robot.sourceCodeProperty().get());
        }
    }

    private Collection<Robot> createRobotsFromViewModel() {
        final Collection<Robot> robots = new ArrayList<>();
        for (final EditRobotViewModel rvm : this.robots) {
            try {
                final Robot robot = createRobotFromViewModel(rvm);
                robots.add(robot);
            } catch(Exception e) {
                this.robotsListView.getSelectionModel().select(rvm);
                this.compilerOutput.setText(e.getMessage());
            }
        }
        return robots;
    }

    private Robot createRobotFromViewModel(EditRobotViewModel robotViewModel) throws Exception {
        final Color color = robotViewModel.colorProperty().get();
        final int rgb = (int)(color.getRed() * 0xff) << 16 |
                (int)(color.getGreen() * 0xff) << 8 |
                (int)(color.getBlue() * 0xff);
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(robotViewModel.sourceCodeProperty().get());
        if (result.hasErrors()) {
            throw new Exception(String.join("\n", result.errors()));
        }
        return new Robot.Builder(r -> new CompiledProgram(r, result.program()))
                .name(robotViewModel.nameProperty().get())
                .rgb(rgb)
                .build();
    }

    @FXML
    private void newRobotMover(ActionEvent actionEvent) {
        newRobotWithProgram("Mover", "robots/mover.jobot");
    }

    @FXML
    private void newRobotSmart(ActionEvent actionEvent) {
        newRobotWithProgram("Smartie", "robots/smart.jobot");
    }

    @FXML
    private void newRobotDumbShooter(ActionEvent actionEvent) {
        newRobotWithProgram("DumbShooter", "robots/dumbshooter.jobot");
    }

    @FXML
    private void newRobotPatrol(ActionEvent actionEvent) {
        newRobotWithProgram("Patrol", "robots/patrol.jobot");
    }

    @FXML
    private void newRobotBumblebee(ActionEvent actionEvent) {
        newRobotWithProgram("Bumblebee", "robots/bumblebee.jobot");
    }

    private static class RobotViewModelCell extends ListCell<EditRobotViewModel> {
        @Override
        protected void updateItem(EditRobotViewModel robotViewModel, boolean empty) {
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
