package net.smackem.jobotwar.gui.edit;

import com.google.common.io.CharStreams;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.smackem.jobotwar.gui.App;
import net.smackem.jobotwar.gui.graphics.RgbConvert;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.persist.PersistableRobots;
import net.smackem.jobotwar.runtime.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

public class EditController {

    private static final Logger log = LoggerFactory.getLogger(EditController.class);
    private final ListProperty<EditRobotViewModel> robots = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<EditRobotViewModel> selectedRobot = new SimpleObjectProperty<>();
    private final Random random = new Random();
    private static final int BOARD_WIDTH = 640;
    private static final int BOARD_HEIGHT = 480;
    private static Collection<EditRobotViewModel> cachedRobotViewModels;

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
    private ComboBox<Image> iconComboBox;
    @FXML
    private Button simulateButton;
    @FXML
    private Pane detailsPane;
    @FXML
    private SplitMenuButton newRobotButton;

    public EditController() {
        if (cachedRobotViewModels != null) {
            this.robots.addAll(cachedRobotViewModels);
        }
    }

    @FXML
    private void initialize() {
        this.newRobotButton.disableProperty().bind(this.robots.sizeProperty().greaterThanOrEqualTo(64));
        this.robotsListView.setItems(this.robots);
        this.robotsListView.getSelectionModel().selectedItemProperty().addListener(
                (prop, old, val) -> selectRobot(val));
        this.robotsListView.setCellFactory(listView -> new RobotViewModelCell());
        this.playButton.disableProperty().bind(
                this.robots.sizeProperty().lessThanOrEqualTo(0));
        this.simulateButton.disableProperty().bind(
                this.robots.sizeProperty().lessThanOrEqualTo(1));
        this.sourceText.textProperty().addListener(
                (prop, old, val) -> this.selectedRobot.get().sourceCodeProperty().set(val));
        this.iconComboBox.setCellFactory(listView -> new IconCell());
        this.iconComboBox.setButtonCell(new IconCell());
        this.iconComboBox.getItems().addAll(
                null,
                getResourceImage("icon-rebelalliance.png"),
                getResourceImage("icon-empire.png"));
        this.iconComboBox.getSelectionModel().selectedItemProperty().addListener(
                (prop, old, val) -> this.selectedRobot.get().imageProperty().set(val));
        if (this.robots.size() == 0) {
            newRobot(null);
        } else {
            selectRobot(this.robots.iterator().next());
        }
        this.detailsPane.visibleProperty().bind(this.selectedRobot.isNotNull());
    }

    private Image getResourceImage(String resourceName) {
        return new Image(String.valueOf(getClass().getResource("/net/smackem/jobotwar/gui/icons/" + resourceName)));
    }

    @FXML
    private void startGame(ActionEvent mouseEvent) {
        initiateGame((app, robots) -> app.startGame(BOARD_WIDTH, BOARD_HEIGHT, robots));
    }

    @FXML
    private void newRobot(ActionEvent mouseEvent) {
        newRobotWithProgram("Robot", null);
    }

    private void newRobotWithProgram(String robotBaseName, String resourceName) {
        final EditRobotViewModel robot = new EditRobotViewModel();
        robot.nameProperty().set(robotBaseName + " " + (this.robots.size() + 1));
        robot.colorProperty().set(getNextRandomColor());
        if (resourceName != null) {
            try (final InputStream stream = getClass().getResourceAsStream(resourceName);
                 final InputStreamReader reader = new InputStreamReader(stream)) {
                robot.sourceCodeProperty().set(CharStreams.toString(reader));
            } catch (Exception ignored) { }
        } else {
            robot.sourceCodeProperty().set("");
        }
        this.robots.add(robot);
        this.robotsListView.getSelectionModel().select(robot);
    }

    private Color getNextRandomColor() {
        for (int i = 0; i < 12; i++) {
            final int newHue = this.random.nextInt(12);
            final boolean isUnique = this.robots.stream()
                    .noneMatch(r -> (int)(r.colorProperty().get().getHue() / 30.0) == newHue);
            if (isUnique) {
                return Color.hsb(newHue * 30, 1.0, 1.0);
            }
        }
        return Color.BLACK;
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
            this.iconComboBox.getSelectionModel().select(robot.imageProperty().get());
        }
    }

    private Collection<Robot> createRobotsFromViewModel() throws Exception {
        final Collection<Robot> robots = new ArrayList<>();
        final Set<String> robotNames = new HashSet<>();
        for (final EditRobotViewModel rvm : this.robots) {
            try {
                if (robotNames.add(rvm.nameProperty().get()) == false) {
                    throw new Exception(String.format("Robot name '%s' is not unique!", rvm.nameProperty().get()));
                }
                final Robot robot = createRobotFromViewModel(rvm);
                robots.add(robot);
            } catch (Exception e) {
                this.robotsListView.getSelectionModel().select(rvm);
                this.compilerOutput.setText(e.getMessage());
                throw e;
            }
        }
        return robots;
    }

    private Robot createRobotFromViewModel(EditRobotViewModel robotViewModel) throws Exception {
        final Color color = robotViewModel.colorProperty().get();
        final int rgb = RgbConvert.toRgb(color);
        final Image image = robotViewModel.imageProperty().get();
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(robotViewModel.sourceCodeProperty().get(), Compiler.Language.V1);
        if (result.hasErrors()) {
            throw new Exception(String.join("\n", result.errors()));
        }
        return App.instance().createRobot(
                result.program(),
                robotViewModel.nameProperty().get(),
                rgb,
                image != null ? image.getUrl() : null);
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

    @FXML
    private void newRobotFastScan(ActionEvent actionEvent) {
        newRobotWithProgram("FastScan", "robots/fastscan.jobot");
    }

    @FXML
    private void newRobotShooter(ActionEvent actionEvent) {
        newRobotWithProgram("Shooter", "robots/shooter.jobot");
    }

    @FXML
    private void newRobotCorner(ActionEvent actionEvent) {
        newRobotWithProgram("Corner", "robots/corner.jobot");
    }

    @FXML
    private void simulateGame(ActionEvent actionEvent) {
        initiateGame((app, robots) -> app.simulateGame(BOARD_WIDTH, BOARD_HEIGHT, robots));
    }

    private void initiateGame(BiConsumer<App, Collection<Robot>> gameMode) {
        final App app = App.instance();
        final Collection<Robot> robots;
        try {
            robots = createRobotsFromViewModel();
            cachedRobotViewModels = new ArrayList<>(this.robots);
        } catch (Exception e) {
            return;
        }
        gameMode.accept(app, robots);
    }

    private static FileChooser createJobotFileChooser() {
        final FileChooser dialog = new FileChooser();
        dialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Jobotwar Robot", "*.jobot"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        return dialog;
    }

    @FXML
    private void openRobotFile(ActionEvent actionEvent) {
        final FileChooser dialog = createJobotFileChooser();
        final Window window = this.sourceText.getScene().getWindow();
        final List<File> files = dialog.showOpenMultipleDialog(window);
        if (files == null) {
            return;
        }
        boolean firstRobot = true;
        for (final File file : files) {
            final EditRobotViewModel robot;
            try (final InputStream is = new FileInputStream(file)) {
                robot = PersistableRobots.load(EditRobotViewModel::new, is);
            } catch (IOException e) {
                log.error("error opening file " + file.getPath(), e);
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
                return;
            }
            robot.colorProperty().set(getNextRandomColor());
            robot.nameProperty().set(robot.getBaseName() + " " + (this.robots.size() + 1));
            this.robots.add(robot);
            if (firstRobot) {
                this.robotsListView.getSelectionModel().select(robot);
                firstRobot = false;
            }
        }
    }

    @FXML
    private void saveRobotFile(ActionEvent actionEvent) {
        final EditRobotViewModel robot = selectedRobot.get();
        if (robot == null) {
            return;
        }
        final FileChooser dialog = createJobotFileChooser();
        final Window window = this.sourceText.getScene().getWindow();
        final File file = dialog.showSaveDialog(window);
        if (file == null) {
            return;
        }
        try (final OutputStream os = new FileOutputStream(file)) {
            PersistableRobots.save(robot, os);
        } catch (IOException e) {
            log.error("error saving file " + file.getPath(), e);
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }

    private static class RobotViewModelCell extends ListCell<EditRobotViewModel> {
        @Override
        protected void updateItem(EditRobotViewModel robotViewModel, boolean empty) {
            super.updateItem(robotViewModel, empty);
            if (empty ) {
                textProperty().unbind();
                setText(null);
                return;
            }
            textProperty().bind(robotViewModel.nameProperty());
        }
    }

    private static class IconCell extends ListCell<Image> {
        @Override
        protected void updateItem(Image item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
                return;
            }
            if (item == null) {
                setGraphic(null);
                setText("(None)");
                return;
            }
            setGraphic(new ImageView(item));
            setText(null);
        }
    }
}
