package nl.juraji.biliomi.utility.calculate;

import nl.juraji.biliomi.utility.estreams.einterface.ESupplier;

/**
 * Created by Juraji on 21-4-2017.
 * Biliomi v3
 */
public final class NumberConverter {
    private final String input;
    private Number defaultValue = null;

    private NumberConverter(String input) {
        this.input = input;
    }

    /**
     * Instanciate a new Parser object with the given string
     *
     * @param input A string representing a number
     * @return A new NumberConverter instance
     */
    public static NumberConverter asNumber(String input) {
        return new NumberConverter(input);
    }

    /**
     * @param defaultValue A default value to return if parsing fails
     * @return this
     */
    public NumberConverter withDefault(Number defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Parse to integer
     *
     * @return The parsed integer or null|default
     */
    public Integer toInteger() {
        return parsedOrDefault(() -> Integer.parseInt(input));
    }


    /**
     * Parse to double
     *
     * @return The parsed double or null|default
     */
    public Double toDouble() {
        return parsedOrDefault(() -> Double.parseDouble(input));
    }

    /**
     * Parse to long
     *
     * @return The parsed long or null|default
     */
    public Long toLong() {
        return parsedOrDefault(() -> Long.parseLong(input));
    }

    /**
     * @return True when input is parseable as number, else false
     */
    public boolean isNaN() {
        return (toDouble() == null);
    }

    /**
     * Run the given Number-like supplier
     *
     * @param parsed A Number-like Suplier
     * @param <T>    The type to return
     * @return The value from the supplier or default
     */
    private <T> T parsedOrDefault(ESupplier<T, NumberFormatException> parsed) {
        if (input != null) {
            try {
                return parsed.get();
            } catch (NumberFormatException ignored) {
            }
        }

        //noinspection unchecked
        return (T) defaultValue;
    }
}
