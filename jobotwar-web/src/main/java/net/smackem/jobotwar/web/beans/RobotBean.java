package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.lang.Compiler;

import java.time.OffsetDateTime;

public class RobotBean extends PersistableBean {
    @JsonProperty private String code;
    @JsonProperty private Compiler.Language language;
    @JsonProperty private String name;
    @JsonProperty private double acceleration;
    @JsonProperty private int rgb;
    @JsonProperty private OffsetDateTime dateCreated;
    @JsonProperty private OffsetDateTime dateModified;

    @JsonCreator
    private RobotBean() {
    }

    public RobotBean(String id) {
        super(id);
    }

    public String code() {
        return this.code;
    }

    public RobotBean code(String code) {
        this.code = code;
        return this;
    }

    public Compiler.Language language() {
        return this.language;
    }

    public RobotBean language(Compiler.Language language) {
        this.language = language;
        return this;
    }

    public String name() {
        return this.name;
    }

    public RobotBean name(String name) {
        this.name = name;
        return this;
    }

    public double acceleration() {
        return this.acceleration;
    }

    public RobotBean acceleration(double acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    public int rgb() {
        return this.rgb;
    }

    public RobotBean rgb(int rgb) {
        this.rgb = rgb;
        return this;
    }

    public OffsetDateTime dateCreated() {
        return this.dateCreated;
    }

    public RobotBean dateCreated(OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public OffsetDateTime dateModified() {
        return this.dateModified;
    }

    public RobotBean dateModified(OffsetDateTime dateModified) {
        this.dateModified = dateModified;
        return this;
    }
}
