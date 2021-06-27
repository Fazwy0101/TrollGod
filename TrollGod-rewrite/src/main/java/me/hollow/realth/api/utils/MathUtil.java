package me.hollow.realth.api.utils;

public class MathUtil {

    public static <T extends Number> T clamp(T value, T minimum, T maximum) {
        return value.floatValue() <= minimum.floatValue() ? minimum : (value.floatValue() >= maximum.floatValue() ? maximum : value);
    }

}
