package net.smackem.jobotwar.lang.v2;

import java.util.ArrayList;
import java.util.Collection;

public class ProcedureDecl {
    final String name;
    final Collection<String> parameters = new ArrayList<>();

    ProcedureDecl(String name) {
        this.name = name;
    }
}
