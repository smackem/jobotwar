package net.smackem.jobotwar.lang.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class ProcedureDecl extends Declaration {
    private final Collection<String> parameters = new ArrayList<>();

    protected ProcedureDecl(String name, int order) {
        super(name, order);
    }

    public Collection<String> parameters() {
        return Collections.unmodifiableCollection(this.parameters);
    }

    public void addParameter(String parameter) {
        if (this.parameters.contains(parameter)) {
            throw new IllegalArgumentException("duplicate parameter name '" + parameter + "'");
        }
        this.parameters.add(parameter);
    }
}
