package nl.juraji.biliomi.utility.types.enums;

/**
 * Created by robin.
 * mei 2017
 */
public enum OnOff {
    ON, OFF;

    public static OnOff fromBoolean(boolean state) {
        return (state ? ON : OFF);
    }
}
