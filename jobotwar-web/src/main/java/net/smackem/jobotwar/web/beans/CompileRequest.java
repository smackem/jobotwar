package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.lang.Compiler;

public class CompileRequest {
    @JsonProperty private final String code;
    @JsonProperty private final Compiler.Language language;
    @JsonProperty private final String robotName;

    @JsonCreator
    private CompileRequest() {
        this.code = null;
        this.language = null;
        this.robotName = null;
    }

    public CompileRequest(String code, Compiler.Language language, String robotName) {
        this.code = code;
        this.language = language;
        this.robotName = robotName;
    }

    public String code() {
        return this.code;
    }

    public Compiler.Language language() {
        return this.language;
    }

    public String robotName() {
        return this.robotName;
    }
}
