package net.smackem.jobotwar.lang.v2;

import net.smackem.jobotwar.lang.common.Emitter;

import java.util.Objects;

class EmittingListenerV2 extends JobotwarV2BaseListener {
    private final Emitter emitter;

    EmittingListenerV2(Emitter emitter) {
        this.emitter = Objects.requireNonNull(emitter);
    }
}
