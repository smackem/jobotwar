package net.smackem.jobotwar.lang.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ProcedureDecl extends Declaration {
    private final List<String> parameters = new ArrayList<>();
    private final List<String> locals = new ArrayList<>();

    protected ProcedureDecl(String name, int order) {
        super(name, order);
    }

    public List<String> parameters() {
        return Collections.unmodifiableList(this.parameters);
    }

    public List<String> locals() {
        return Collections.unmodifiableList(this.locals);
    }

    public void addParameter(String parameter) {
        if (this.parameters.contains(parameter)) {
            throw new IllegalArgumentException("duplicate parameter name '" + parameter + "'");
        }
        this.parameters.add(parameter);
    }

    public void addLocal(String local) {
        if (this.parameters.contains(local) || this.locals.contains(local)) {
            throw new IllegalArgumentException("duplicate local name '" + local + "'");
        }
        this.locals.add(local);
    }

    public int findLocalOrParameter(String name) {
        int index = this.parameters.indexOf(name);
        if (index >= 0) {
            return index;
        }
        index = this.locals.indexOf(name);
        if (index >= 0) {
            return this.parameters.size() + index;
        }
        return -1;
    }
}
