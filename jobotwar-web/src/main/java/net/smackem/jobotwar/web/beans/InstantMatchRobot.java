package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.lang.Compiler;

import java.util.Objects;

public class InstantMatchRobot {
    @JsonProperty private final String name;
    @JsonProperty private String code;
    @JsonProperty private Compiler.Language language = Compiler.Language.V2;
    @JsonProperty private double x;
    @JsonProperty private double y;

    @JsonCreator
    private InstantMatchRobot() {
        this.name = null;
    }

    public InstantMatchRobot(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String code() {
        return this.code;
    }

    public InstantMatchRobot code(String code) {
        this.code = code;
        return this;
    }

    public Compiler.Language language() {
        return this.language;
    }

    public InstantMatchRobot language(Compiler.Language language) {
        this.language = language;
        return this;
    }

    public double x() {
        return this.x;
    }

    public InstantMatchRobot x(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return this.y;
    }

    public InstantMatchRobot y(double y) {
        this.y = y;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstantMatchRobot robotDto = (InstantMatchRobot) o;
        return Double.compare(robotDto.x, x) == 0 && Double.compare(robotDto.y, y) == 0 && Objects.equals(name, robotDto.name) && Objects.equals(code, robotDto.code) && language == robotDto.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, language, x, y);
    }
}
