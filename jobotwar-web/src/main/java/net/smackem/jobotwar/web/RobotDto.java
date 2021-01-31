package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.lang.Compiler;

class RobotDto {
    @JsonProperty private final String name;
    @JsonProperty private String code;
    @JsonProperty private Compiler.Language language = Compiler.Language.V2;
    @JsonProperty private double x;
    @JsonProperty private double y;

    public RobotDto(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String code() {
        return this.code;
    }

    public RobotDto code(String code) {
        this.code = code;
        return this;
    }

    public Compiler.Language language() {
        return this.language;
    }

    public RobotDto setLanguage(Compiler.Language language) {
        this.language = language;
        return this;
    }

    public double x() {
        return this.x;
    }

    public RobotDto x(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return this.y;
    }

    public RobotDto y(double y) {
        this.y = y;
        return this;
    }
}
