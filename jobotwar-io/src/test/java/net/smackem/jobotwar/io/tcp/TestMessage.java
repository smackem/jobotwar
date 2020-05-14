package net.smackem.jobotwar.io.tcp;

public final class TestMessage {
    private TestMessage() {
        throw new IllegalAccessError();
    }

    public interface Base {}
    public static record Chat(String text) implements Base {}
    public static record ClientConnected(String clientAddress) implements Base {}
}
