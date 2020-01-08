package net.smackem.jobotwar.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.Robot;

import java.io.IOException;
import java.util.Collection;

/**
 * JavaFX App
 */
public class App extends Application {

    private Scene scene;
    private Board board;
    private static App INSTANCE;

    public App() {
        if (INSTANCE != null) {
            throw new RuntimeException("only one instance allowed!");
        }
        INSTANCE = this;
    }

    public static App instance() {
        return INSTANCE;
    }

    public Board board() {
        return this.board;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.scene = new Scene(loadFXML("edit"), 850, 650);
        stage.setScene(this.scene);
        stage.show();
    }

    public void startGame(int width, int height, Collection<Robot> robots) throws IOException {
        this.board = new Board(width, height, robots);
        this.board.disperseRobots();
        setRoot("main");
    }

    public void showEditor() throws IOException {
        setRoot("edit");
    }

    private void setRoot(String fxml) throws IOException {
        this.scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}