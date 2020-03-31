package net.smackem.jobotwar.runtime;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class GameRecorder implements RobotProgramContext {
    private final Random random;
    private final Board board;
    private final Board replayBoard;
    private final Map<String, RobotRecord> robotRecords = new HashMap<>();
    private Consumer<RobotLogMessage> robotMessageLogger;
    private Mode mode;

    public GameRecorder(Random random, Function<RobotProgramContext, Board> boardFactory) {
        this.random = Objects.requireNonNull(random);
        this.board = Objects.requireNonNull(boardFactory).apply(this);
        this.mode = Mode.RECORD;
        for (final Robot r : this.board.robots()) {
            if (this.robotRecords.putIfAbsent(r.name(), new RobotRecord()) != null) {
                throw new IllegalArgumentException(String.format("robot name '%s' is not unique in board", r.name()));
            }
        }
        this.replayBoard = Board.fromTemplate(this.board, this);
    }

    public enum Mode { RECORD, PLAY }

    public Mode mode() {
        return this.mode;
    }

    public Board replayBoard() {
        return this.replayBoard;
    }

    public Board replay(Consumer<RobotLogMessage> robotMessageLogger) {
        this.mode = Mode.PLAY;
        this.robotMessageLogger = robotMessageLogger;
        for (final RobotRecord record : this.robotRecords.values()) {
            record.reset();
        }
        return this.replayBoard;
    }

    public Board board() {
        return this.board;
    }

    @Override
    public void logMessage(Robot robot, String category, double value) {
        switch (this.mode) {
            case RECORD -> {
                this.robotRecords.get(robot.name()).logMessages.add(new RobotLogMessage(robot.name(), category, value));
            }
            case PLAY -> {
                if (this.robotMessageLogger != null) {
                    this.robotMessageLogger.accept(new RobotLogMessage(robot.name(), category, value));
                }
            }
        }
    }

    @Override
    public double nextRandomDouble(Robot robot) {
        return switch (this.mode) {
            case RECORD -> recordRandomDouble(robot);
            case PLAY -> playRandomDouble(robot);
        };
    }

    private double recordRandomDouble(Robot r) {
        final double number = this.random.nextDouble();
        this.robotRecords.get(r.name()).randomNumbers.add(number);
        return number;
    }

    private double playRandomDouble(Robot r) {
        final RobotRecord record = this.robotRecords.get(r.name());
        assert record != null;
        return record.randomNumberIterator.hasNext()
                ? record.randomNumberIterator.next()
                : 0; // occurs when game has ended, but winner keeps on drawing random numbers
    }

    private static class RobotRecord {
        final Collection<Double> randomNumbers = new ArrayList<>();
        final Collection<RobotLogMessage> logMessages = new ArrayList<>();
        Iterator<Double> randomNumberIterator;

        void reset() {
            this.randomNumberIterator = this.randomNumbers.iterator();
        }
    }
}
