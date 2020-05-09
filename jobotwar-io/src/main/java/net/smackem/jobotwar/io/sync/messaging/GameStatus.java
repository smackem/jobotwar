package net.smackem.jobotwar.io.sync.messaging;

import java.util.Arrays;
import java.util.Objects;

public enum GameStatus {
    PLAY("PLAY"),
    PAUSE("PAUSE"),
    READY("READY");

    private final String status;

    GameStatus(String state) {
        this.status = state;
    }

    @Override
    public String toString() {
        return this.status;
    }

    public static GameStatus parse(String str) {
        return Arrays.stream(GameStatus.values())
                .filter(v -> Objects.equals(v.status, str))
                .findFirst()
                .orElseThrow();
    }
}
