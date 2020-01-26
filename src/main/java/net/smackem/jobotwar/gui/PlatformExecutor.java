package net.smackem.jobotwar.gui;

import javafx.application.Platform;

import java.util.concurrent.Executor;

public enum PlatformExecutor implements Executor {
    INSTANCE;

    @Override
    public void execute(Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        Platform.runLater(runnable);
    }
}
