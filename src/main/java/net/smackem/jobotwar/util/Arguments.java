package net.smackem.jobotwar.util;

public class Arguments {
    private Arguments() throws IllegalAccessError {
        throw new IllegalAccessError();
    }

    public static int requireRange(int value, int min, int max) {
        if (value < min) {
            throw new IllegalArgumentException(String.format("value %d is less than minimum %d", value, min));
        }
        if (value > max) {
            throw new IllegalArgumentException(String.format("value %d is greater than maximum %d", value, max));
        }
        return value;
    }
}
