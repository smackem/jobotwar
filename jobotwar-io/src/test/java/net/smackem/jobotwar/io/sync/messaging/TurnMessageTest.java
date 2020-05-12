package net.smackem.jobotwar.io.sync.messaging;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TurnMessageTest {

    @Test
    public void decode() {
        final String source = """
                {
                    "turn": {
                        "turnid": 1,
                        "robots": {
                            "3187d1e5-f298-464a-bba4-6f68b4efc326": {
                                 "radar": 123.34,
                                 "aim": 123.35,
                                 "random": 4711.42,
                                 "shot": 1000.0,
                                 "speedX": 54.3,
                                 "speedY": 34.2
                            },
                            "3187d1e5-f298-464a-bba4-6f68b4efc327": {
                                 "speedX": 54.4,
                                 "speedY": 34.3
                            }
                        }
                    }
                }
                """;
        final Message message = Message.decode(MessageTests.createStringInput(source));
        assertThat(message).isNotNull();
        assertThat(message).isInstanceOf(TurnMessage.class);
        final TurnMessage turn = (TurnMessage) message;
        assertThat(turn.turnId()).isEqualTo(1);
        assertThat(turn.robotStateChanges().get(UUID.randomUUID())).isNull();
        final RobotStateChange robot1 = turn.robotStateChanges().get(UUID.fromString("3187d1e5-f298-464a-bba4-6f68b4efc326"));
        assertThat(robot1).isNotNull();
        assertThat(robot1.radar()).isPresent().hasValue(123.34);
        assertThat(robot1.aim()).isPresent().hasValue(123.35);
        assertThat(robot1.random()).isPresent().hasValue(4711.42);
        assertThat(robot1.shot()).isPresent().hasValue(1000.0);
        assertThat(robot1.speedX()).isPresent().hasValue(54.3);
        assertThat(robot1.speedY()).isPresent().hasValue(34.2);
        final RobotStateChange robot2 = turn.robotStateChanges().get(UUID.fromString("3187d1e5-f298-464a-bba4-6f68b4efc327"));
        assertThat(robot2).isNotNull();
        assertThat(robot2.radar()).isNotPresent();
        assertThat(robot2.aim()).isNotPresent();
        assertThat(robot2.random()).isNotPresent();
        assertThat(robot2.shot()).isNotPresent();
        assertThat(robot2.speedX()).isPresent().hasValue(54.4);
        assertThat(robot2.speedY()).isPresent().hasValue(34.3);
    }

    @Test
    public void encode() {
        final UUID robotId1 = UUID.randomUUID();
        final UUID robotId2 = UUID.randomUUID();
        final byte[] bytes = MessageTests.encodeMessage(new TurnMessage.Builder(2)
                .addRobotStateChange(robotId1, new RobotStateChange.Builder()
                        .aim(1.5)
                        .radar(2.5)
                        .random(3.5)
                        .shot(4.5)
                        .speedX(5.5)
                        .speedY(6.5)
                        .build())
                .addRobotStateChange(robotId2, new RobotStateChange.Builder()
                        .aim(77)
                        .shot(99)
                        .build())
                .build());
        final Message decodedMessage = Message.decode(new ByteArrayInputStream(bytes));
        assertThat(decodedMessage).isNotNull();
        assertThat(decodedMessage).isInstanceOf(TurnMessage.class);
    }
}