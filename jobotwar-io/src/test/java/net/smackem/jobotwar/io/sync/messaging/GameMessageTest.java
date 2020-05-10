package net.smackem.jobotwar.io.sync.messaging;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class GameMessageTest {

    @Test
    public void encode() {
        final String source = """
                {
                    "game": {
                         "status": "PLAY"
                    }
                }
                """;
        final InputStream stream = MessageTests.createStringInput(source);
        final Message message = Message.decode(stream);
        assertThat(message).isNotNull().isInstanceOf(GameMessage.class);
        assertThat(((GameMessage)message).status()).isEqualTo(GameStatus.PLAY);
    }

    @Test
    public void decode() {
        final var bytes = MessageTests.encodeMessage(new GameMessage(GameStatus.PAUSE));
        final Message decodedMessage = Message.decode(new ByteArrayInputStream(bytes));
        assertThat(decodedMessage).isNotNull().isInstanceOf(GameMessage.class);
        assertThat(((GameMessage)decodedMessage).status()).isEqualTo(GameStatus.PAUSE);
    }
}