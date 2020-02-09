package net.smackem.jobotwar.gui.graphics;

import javafx.scene.paint.Color;

/**
 * Provides methods to convert a {@link Color} to an integer RGB (0x00RRGGBB) and vice versa.
 */
public class RgbConvert {

    private RgbConvert() {
        throw new IllegalAccessError();
    }

    /**
     * Converts a {@link Color} to an integer that contains all three channel values
     * in the format 0x00RRGGBB
     */
    public static int toRgb(Color color) {
        return (int)(color.getRed() * 0xff) << 16 |
                (int)(color.getGreen() * 0xff) << 8 |
                (int)(color.getBlue() * 0xff);
    }

    /**
     * Converts an 0x00RRGGBB integer to a {@link Color}.
     */
    public static Color toColor(int rgb) {
        return Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff);
    }

    /**
     * Converts an 0x00RRGGBB integer to a {@link Color} with the specified {@code opacity} in the range 0..1.
     */
    public static Color toColor(int rgb, double opacity) {
        return Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, opacity);
    }
}
