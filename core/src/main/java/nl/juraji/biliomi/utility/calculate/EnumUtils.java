package nl.juraji.biliomi.utility.calculate;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by robin.
 * mei 2017
 */
public final class EnumUtils {

    private EnumUtils() {
    }

    /**
     * Convert the given name to an enum value
     * Case insensitive, Expects E to have uppercase values
     * Based on org.apache.commons.lang3.EnumUtils.getEnum()
     *
     * @param name      The name to convert
     * @param enumClass The Enum class to convert to
     * @param <E>       Enum type
     * @return The matching enum value or null when not convertible
     */
    public static <E extends Enum<E>> E toEnum(String name, Class<E> enumClass) {
        return toEnum(name, enumClass, null);
    }

    /**
     * Convert the given name to an enum value
     * Case insensitive, Expects E to have uppercase values
     * Based on org.apache.commons.lang3.EnumUtils.getEnum()
     *
     * @param name        The name to convert
     * @param enumClass   The Enum class to convert to
     * @param defaultName The default to return on inconvertable value
     * @param <E>         Enum type
     * @return The matching enum value or null when not convertible
     */
    public static <E extends Enum<E>> E toEnum(String name, Class<E> enumClass, E defaultName) {
        if (name == null) {
            return defaultName;
        } else {
            try {
                return Enum.valueOf(enumClass, name.toUpperCase());
            } catch (IllegalArgumentException var3) {
                return defaultName;
            }
        }
    }

    /**
     * Prettify an enumerator field by the following rules:
     * 1. Split by lower dash ("_")
     * 2. Lowercase each word
     * 3. Capitalize each word
     * 4. paste everything together separated by a space
     *
     * @param field The field to pretify
     * @return A prettified string as above
     */
    public static String pretty(Enum<?> field) {
        return Arrays.stream(field.toString().split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .reduce((l, r) -> l + ' ' + r)
                .orElse(field.toString());
    }

    /**
     * Check of to enum values equal (Nullsafe)
     *
     * @param e1  Enum 1
     * @param e2  Enum 2
     * @param <E> Generic type to force type equality
     * @return True when equals, False when not equals or null
     */
    public static <E extends Enum<E>> boolean equals(Enum<E> e1, Enum<E> e2) {
        return !(e1 == null || e2 == null) && e2.equals(e1);
    }
}
