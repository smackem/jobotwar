package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.InitMessage;
import net.smackem.jobotwar.io.sync.messaging.RobotStateChange;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RemoteSyncClient {

    private final Map<UUID, RobotState> robots = new HashMap<>();
    private final SocketAddress address;

    public RemoteSyncClient(SocketAddress address) {
        this.address = address;
    }

    public SocketAddress address() {
        return this.address;
    }

    Map<UUID, RobotState> robots() {
        return this.robots;
    }

    record RobotState(InitMessage initial, RobotStateChange latest) {}
}
