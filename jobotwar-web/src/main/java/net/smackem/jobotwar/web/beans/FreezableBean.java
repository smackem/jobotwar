package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class FreezableBean {
    @JsonIgnore private transient boolean frozen;

    public boolean isFrozen() {
        return this.frozen;
    }

    public <T extends FreezableBean> T freeze() {
        this.frozen = true;
        //noinspection unchecked
        return (T) this;
    }

    protected void assertMutable() {
        if (this.frozen) {
            throw new UnsupportedOperationException("this object is frozen and cannot me modified.");
        }
    }
}
