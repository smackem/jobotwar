package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.smackem.jobotwar.lang.Compiler;

import java.time.OffsetDateTime;
import java.util.Objects;

public class RobotBean extends EntityBean {
    @JsonProperty private String code;
    @JsonProperty private Compiler.Language language = Compiler.Language.V1;
    @JsonProperty private String name;
    @JsonProperty private double acceleration;
    @JsonProperty private int rgb;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime dateCreated;

    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    private OffsetDateTime dateModified;

    @JsonCreator
    private RobotBean() {
    }

    @JsonSetter("language")
    private void setLanguage(Compiler.Language language) {
        this.language = language != null ? language : Compiler.Language.V1;
    }

    public RobotBean(String id) {
        super(id);
    }

    public String code() {
        return this.code;
    }

    public RobotBean code(String code) {
        assertMutable();
        this.code = code;
        return this;
    }

    public Compiler.Language language() {
        return this.language;
    }

    public RobotBean language(Compiler.Language language) {
        assertMutable();
        this.language = language;
        return this;
    }

    public String name() {
        return this.name;
    }

    public RobotBean name(String name) {
        assertMutable();
        this.name = name;
        return this;
    }

    public double acceleration() {
        return this.acceleration;
    }

    public RobotBean acceleration(double acceleration) {
        assertMutable();
        this.acceleration = acceleration;
        return this;
    }

    public int rgb() {
        return this.rgb;
    }

    public RobotBean rgb(int rgb) {
        assertMutable();
        this.rgb = rgb;
        return this;
    }

    public OffsetDateTime dateCreated() {
        return this.dateCreated;
    }

    public RobotBean dateCreated(OffsetDateTime dateCreated) {
        assertMutable();
        this.dateCreated = dateCreated;
        return this;
    }

    public OffsetDateTime dateModified() {
        return this.dateModified;
    }

    public RobotBean dateModified(OffsetDateTime dateModified) {
        assertMutable();
        this.dateModified = dateModified;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final RobotBean robotBean = (RobotBean) o;
        return Double.compare(robotBean.acceleration, acceleration) == 0 &&
               rgb == robotBean.rgb &&
               Objects.equals(code, robotBean.code) &&
               language == robotBean.language &&
               Objects.equals(name, robotBean.name) &&
               Objects.equals(dateCreated, robotBean.dateCreated) &&
               Objects.equals(dateModified, robotBean.dateModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code, language, name, acceleration, rgb, dateCreated, dateModified);
    }
}
