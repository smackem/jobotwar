package net.smackem.jobotwar.gui;

import javafx.scene.paint.Color;

public class RgbConvert {

    private RgbConvert() {
        throw new IllegalAccessError();
    }

    public static int toRgb(Color color) {
        return (int)(color.getRed() * 0xff) << 16 |
                (int)(color.getGreen() * 0xff) << 8 |
                (int)(color.getBlue() * 0xff);
    }

    public static Color toColor(int rgb) {
        return Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff);
    }
}
