package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.Constants;

public class GameInfoBean {
    @JsonProperty private final String gameVersion;
    @JsonProperty private final int maxBoardWidth;
    @JsonProperty private final int maxBoardHeight;
    @JsonProperty private final int maxRobotHealth;
    @JsonProperty private final double maxRobotAcceleration;
    @JsonProperty private final double robotRadius;
    @JsonProperty private final double explosionRadius;
    @JsonProperty private final double maxRobotSpeed;
    @JsonProperty private final double frameDurationMillis;

    public GameInfoBean() {
        frameDurationMillis = Constants.TICK_DURATION.toMillis();
        maxRobotSpeed = Constants.MAX_ROBOT_GAME_SPEED;
        explosionRadius = Constants.EXPLOSION_RADIUS;
        robotRadius = Constants.ROBOT_RADIUS;
        maxRobotAcceleration = Constants.MAX_ROBOT_ACCELERATION;
        maxRobotHealth = Constants.MAX_HEALTH;
        maxBoardHeight = Constants.MAX_BOARD_HEIGHT;
        maxBoardWidth = Constants.MAX_BOARD_WIDTH;
        gameVersion = Constants.GAME_VERSION;
    }
}
