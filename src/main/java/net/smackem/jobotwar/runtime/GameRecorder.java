package net.smackem.jobotwar.runtime;

import java.util.*;
import java.util.function.Function;

public final class GameRecorder implements RobotProgramContext {
    private final Random random;
    private final Board board;
    private final Board replayBoard;
    private final Map<String, RobotRecord> robotRecords = new HashMap<>();
    private Mode mode;

    public GameRecorder(Random random, Function<RobotProgramContext, Board> boardFactory) {
        this.random = Objects.requireNonNull(random);
        this.board = Objects.requireNonNull(boardFactory).apply(this);
        this.mode = Mode.RECORD;
        for (final Robot r : this.board.robots()) {
            this.robotRecords.put(r.name(), new RobotRecord());
        }
        this.replayBoard = Board.fromTemplate(this.board, this);
    }

    public enum Mode { RECORD, PLAY }

    public Mode mode() {
        return this.mode;
    }

    public Board replay() {
        this.mode = Mode.PLAY;
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
            case RECORD:
                this.robotRecords.get(robot.name()).logMessages.add(new RobotLogMessage(category, value));
                break;
            case PLAY:
                break;
        }
    }

    @Override
    public double nextRandomDouble(Robot robot) {
        switch (this.mode) {
            case RECORD:    return recordRandomDouble(robot);
            case PLAY:      return playRandomDouble(robot);
            default:
                throw new IllegalStateException("Unexpected value: " + this.mode);
        }
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

    private static class RobotLogMessage {
        final String category;
        final double value;

        RobotLogMessage(String category, double value) {
            this.category = category;
            this.value = value;
        }
    }
}
