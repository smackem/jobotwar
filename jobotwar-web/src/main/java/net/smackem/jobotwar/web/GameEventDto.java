package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonProperty;

class GameEventDto {
    @JsonProperty private final int gameTimeMillis;
    @JsonProperty private final String event;

    public GameEventDto(int gameTimeMillis, String event) {
        this.gameTimeMillis = gameTimeMillis;
        this.event = event;
    }

    public int gameTimeMillis() {
        return this.gameTimeMillis;
    }

    public String event() {
        return this.event;
    }
}
