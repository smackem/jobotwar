package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class PersistableBean {
    @JsonProperty private final String id;

    @JsonCreator
    PersistableBean() {
        this.id = null;
    }

    public PersistableBean(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }
}
