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
import net.smackem.jobotwar.runtime.RobotProgramContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

/**
 * JavaFX App
 */
public class App extends Application {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final EventBus eventBus;
    private Scene scene;
    private Board board;
    private static App INSTANCE;
    private final Random random = new Random();
    private final RobotProgramContext defaultRobotContext = new RobotProgramContext() {
        @Override
        public void logMessage(Robot robot, String category, double value) {
            eventBus.post(new RobotLogMessage(robot, category, value));
        }

        @Override
        public double nextRandomDouble(Robot robot) {
            return random.nextDouble();
        }
    };

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
        this.scene = new Scene(loadFXML("edit/edit.fxml"), 850, 650);
        stage.setScene(this.scene);
        stage.show();
    }

    public Robot createRobot(Program program, String name, int rgb, String imageUrl) {
        return new Robot.Builder(r -> new CompiledProgram(r, program, this.defaultRobotContext))
                .name(name)
                .rgb(rgb)
                .imageUrl(imageUrl)
                .build();
    }

    public void startGame(int width, int height, Collection<Robot> robots) {
        this.board = new Board(width, height, robots);
        this.board.disperseRobots();
        setRoot("main/main.fxml");
    }

    public void simulateGame(int width, int height, Collection<Robot> robots) {
        this.board = new Board(width, height, robots);
        setRoot("simulation/simulation.fxml");
    }

    public void showEditor() throws IOException {
        setRoot("edit/edit.fxml");
    }

    private void setRoot(String fxml) {
        try {
            this.scene.setRoot(loadFXML(fxml));
        } catch (IOException e) {
            log.error("error loading fxml", e);
        }
    }

    private static Parent loadFXML(String fxmlFile) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFile));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}