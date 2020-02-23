package net.smackem.jobotwar.lang.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class ProcedureDecl extends Declaration {
    private final Collection<String> parameters = new ArrayList<>();
    private final Collection<String> locals = new ArrayList<>();

    protected ProcedureDecl(String name, int order) {
        super(name, order);
    }

    public Collection<String> parameters() {
        return Collections.unmodifiableCollection(this.parameters);
    }

    public Collection<String> locals() {
        return Collections.unmodifiableCollection(this.locals);
    }

    public void addParameter(String parameter) {
        if (this.parameters.contains(parameter)) {
            throw new IllegalArgumentException("duplicate parameter name '" + parameter + "'");
        }
        this.parameters.add(parameter);
    }

    public void addLocal(String local) {
        if (this.locals.contains(local)) {
            throw new IllegalArgumentException("duplicate local name '" + local + "'");
        }
        this.locals.add(local);
    }
}
