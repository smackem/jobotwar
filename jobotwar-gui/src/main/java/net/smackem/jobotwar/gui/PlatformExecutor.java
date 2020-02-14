package net.smackem.jobotwar.gui;

import javafx.application.Platform;

import java.util.concurrent.Executor;

/**
 * Singleton {@link Executor} that executes the passed {@link Runnable} on the JavaFX
 * application thread.
 */
public enum PlatformExecutor implements Executor {
    /**
     * The one and only instance.
     */
    INSTANCE;

    @Override
    public void execute(@SuppressWarnings("NullableProblems") Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }
        Platform.runLater(runnable);
    }
}
