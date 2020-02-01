package net.smackem.jobotwar.gui;

import com.google.common.eventbus.EventBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.Robots;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final EventBus eventBus;
    private final Collection<EditRobotViewModel> cachedRobotViewModels = new ArrayList<>();
    private Scene scene;
    private Board board;
    private static App INSTANCE;

    public App() {
        if (INSTANCE != null) {
            throw new RuntimeException("only one instance allowed!");
        }
        INSTANCE = this;
        this.eventBus = new EventBus();
    }

    public static App instance() {
        return INSTANCE;
    }

    public Board board() {
        return this.board;
    }

    public EventBus eventBus() {
        return this.eventBus;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.scene = new Scene(loadFXML("edit"), 850, 650);
        stage.setScene(this.scene);
        stage.show();
    }

    public Robot createRobot(Program program, String name, int rgb, String imageUrl) {
        return new Robot.Builder(r -> new CompiledProgram(r, program, this::logRobotMessage))
                .name(name)
                .rgb(rgb)
                .imageUrl(imageUrl)
                .build();
    }

    public void cacheRobotViewModels(Collection<EditRobotViewModel> robotViewModels) {
        this.cachedRobotViewModels.clear();
        this.cachedRobotViewModels.addAll(robotViewModels);
    }

    public Collection<EditRobotViewModel> getCachedRobotViewModels() {
        return Collections.unmodifiableCollection(this.cachedRobotViewModels);
    }

    public Board copyBoard() {
        final Collection<Robot> newRobots = this.board.robots().stream()
                .map(Robots::buildLike)
                .collect(Collectors.toList());
        final Board newBoard = new Board(this.board.width(), this.board.height(), newRobots);
        newBoard.disperseRobots();
        return newBoard;
    }

    public void startGame(int width, int height, Collection<Robot> robots) {
        this.board = new Board(width, height, robots);
        this.board.disperseRobots();
        setRoot("main");
    }

    public void simulateGame(int width, int height, Collection<Robot> robots) {
        this.board = new Board(width, height, robots);
        setRoot("simulation");
    }

    public void showEditor() throws IOException {
        setRoot("edit");
    }

    private void logRobotMessage(Robot robot, String category, double value) {
        this.eventBus.post(new RobotLogMessage(robot, category, value));
    }

    private void setRoot(String fxml) {
        try {
            this.scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            log.error("error loading fxml", e);
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}