package net.smackem.jobotwar.io.sync.messaging;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class InitMessageTest {

    @Test
    public void decode() {
        final String source = """
                {
                    "init": {
                        "robots": {
                            "3187d1e5-f298-464a-bba4-6f68b4efc326": {
                                "name": "Robot 1",
                                "color": "#d0ff00",
                                "x": 100.3,
                                "y": 200.6,
                                "ready": true
                            },
                            "c60dcff1-3ba9-4ba6-bbe1-8d529f4ab95b": {
                                "name": "Robot 2",
                                "color": "#d0ff80",
                                "x": 150.3,
                                "y": 500.6,
                                "ready": false
                            }
                        }
                    }
                }
                """;
        final Message message = Message.decode(MessageTests.createStringInput(source));
        assertThat(message).isNotNull();
        assertThat(message).isInstanceOf(InitMessage.class);
        final InitMessage init = (InitMessage) message;
        assertThat(init.robots().get(UUID.randomUUID())).isNull();
        final RobotInfo robot1 = init.robots().get(UUID.fromString("3187d1e5-f298-464a-bba4-6f68b4efc326"));
        assertThat(robot1).isNotNull();
        assertThat(robot1.name()).isPresent().hasValue("Robot 1");
        assertThat(robot1.color()).isPresent().hasValue("#d0ff00");
        assertThat(robot1.x()).isPresent().hasValue(100.3);
        assertThat(robot1.y()).isPresent().hasValue(200.6);
        assertThat(robot1.isReady()).isPresent().hasValue(true);
        final RobotInfo robot2 = init.robots().get(UUID.fromString("c60dcff1-3ba9-4ba6-bbe1-8d529f4ab95b"));
        assertThat(robot2).isNotNull();
    }

    @Test
    public void decodeRepeatedly() {
        for (int i = 0; i < 1000; i++) {
            decode();
        }
    }

    @Test
    public void encode() {
        final UUID robotId1 = UUID.randomUUID();
        final UUID robotId2 = UUID.randomUUID();
        final byte[] bytes = MessageTests.encodeMessage(new InitMessage.Builder()
                .addRobot(robotId1, new RobotInfo.Builder()
                        .name("gurke")
                        .x(100.125)
                        .ready(false)
                        .build())
                .addRobot(robotId2, new RobotInfo.Builder()
                        .name("sellerie")
                        .color("#55dd33")
                        .y(554.25)
                        .build())
                .build());
        final Message decodedMessage = Message.decode(new ByteArrayInputStream(bytes));
        assertThat(decodedMessage).isNotNull();
        assertThat(decodedMessage).isInstanceOf(InitMessage.class);
        final InitMessage init = (InitMessage) decodedMessage;
        assertThat(init.robots().get(UUID.randomUUID())).isNull();
        final RobotInfo robot1 = init.robots().get(robotId1);
        assertThat(robot1).isNotNull();
        assertThat(robot1.name()).isPresent().hasValue("gurke");
        assertThat(robot1.color()).isNotPresent();
        assertThat(robot1.x()).isPresent().hasValue(100.125);
        assertThat(robot1.y()).isNotPresent();
        assertThat(robot1.isReady()).isPresent().hasValue(false);
        final RobotInfo robot2 = init.robots().get(robotId2);
        assertThat(robot2).isNotNull();
        assertThat(robot2.name()).isPresent().hasValue("sellerie");
        assertThat(robot2.color()).isPresent().hasValue("#55dd33");
        assertThat(robot2.x()).isNotPresent();
        assertThat(robot2.y()).isPresent().hasValue(554.25);
        assertThat(robot2.isReady()).isNotPresent();
    }
}