package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.common.Emitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

class EmittingListenerV2 extends JobotwarV2BaseListener {
    private final Emitter emitter;
    private final DeclarationsExtractor declarations;

    EmittingListenerV2(Emitter emitter, DeclarationsExtractor declarations) {
        this.emitter = Objects.requireNonNull(emitter);
        this.declarations = declarations;

        int address = 0;
        final List<VariableDecl> globals = new ArrayList<>(this.declarations.globals.values());
        globals.sort(Comparator.comparingInt(a -> a.order));

        for (final VariableDecl variable : globals) {
            emitter.emit(OpCode.LD_F64, 0.0);
            variable.setAddress(address);
            address++;
        }
    }
}
