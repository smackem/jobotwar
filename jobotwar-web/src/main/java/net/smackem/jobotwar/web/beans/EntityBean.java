package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public abstract class EntityBean extends FreezableBean {
    @JsonProperty private String id;

    @JsonCreator
    EntityBean() {
        this.id = null;
    }

    public EntityBean(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    public <T extends EntityBean> T id(String id) {
        this.id = id;
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EntityBean that = (EntityBean) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
