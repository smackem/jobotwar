package net.smackem.jobotwar.io.sync.messaging;

import javax.json.stream.JsonGenerator;

public class GameMessage extends Message {
    private final GameStatus status;

    public GameMessage(GameStatus state) {
        this.status = state;
    }

    public GameStatus status() {
        return this.status;
    }

    @Override
    void encode(JsonGenerator json) {
        json.writeStartObject("game")
                .write("status", this.status.toString())
                .writeEnd();
    }
}
