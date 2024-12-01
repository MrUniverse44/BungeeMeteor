package me.blueslime.bungeemeteor.colors;

import me.blueslime.bungeeutilitiesapi.color.ColorHandler;

public abstract class TextUtilities {
    /**
     * Convert a text in a color text
     * @param message for the conversion
     * @return converted messages with colors and hex colors
     * Supports &#CODE and &CODE formats.
     */
    public static String colorize(String message) {
        return convert(message);
    }

    /**
     * Convert a text in a color text
     * @param message for the conversion
     * @return converted messages with colors and hex colors
     * Supports &#CODE and &CODE formats.
     */
    public static String convert(String message) {
        return ColorHandler.convert(message);
    }
}