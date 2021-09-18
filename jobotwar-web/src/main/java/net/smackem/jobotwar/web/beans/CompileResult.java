package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class CompileResult {
    @JsonProperty private final String program;

    @JsonCreator
    private CompileResult() {
        this.program = null;
    }

    public CompileResult(String program) {
        this.program = Objects.requireNonNull(program);
    }

    public String program() {
        return this.program;
    }
}
