package nl.juraji.biliomi.utility.calculate;

import nl.juraji.biliomi.utility.types.Counter;
import nl.juraji.biliomi.utility.types.MutableString;
import nl.juraji.biliomi.utility.types.collections.FastList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternUtils {
    private static final Pattern LINK = Pattern.compile("([^\\s]+[a-z0-9-@]+\\.[a-z0-9]+[^\\s]+)", Pattern.CASE_INSENSITIVE);

    private PatternUtils() {
    }

    /**
     * Get a list of found links within a message
     *
     * @param message The message to deobfuscate and scan
     * @return A List of matching strings, or empty if none matched
     */
    public static List<String> getLinks(String message) {
        String deobfuscated = deobfuscateLinks(message);
        Matcher matcher = LINK.matcher(deobfuscated);
        List<String> links = new FastList<>();
        while (matcher.find()) {
            links.add(matcher.group());
        }
        return links;
    }

    /**
     * Get the longest sequence of repeating characters
     *
     * @param message The message to evaluate
     * @return The length of the longest sequence
     */
    public static int getLongestRepeatedCharacterSequence(String message) {
        List<Integer> repititionCounts = new ArrayList<>();
        char[] chars = message.replaceAll(" ", "").toCharArray();
        int previous = -1;
        Counter counter = new Counter(1);

        for (int c : chars) {
            if (c == previous || (!Character.isLetterOrDigit(c) && !Character.isLetterOrDigit(previous))) {
                counter.getAndIncrement();
            } else if (counter.isMoreThan(1)) {
                repititionCounts.add(counter.get());
                counter.set(1);
            }
            previous = c;
        }

        // One final readout of the counter for the last chain in the message
        if (counter.isMoreThan(1)) {
            repititionCounts.add(counter.get());
        }

        return repititionCounts.stream()
                .mapToInt(i -> i)
                .max()
                .orElse(0);
    }

    /**
     * Deobfuscate links
     * (Because some people are just sad)
     *
     * @param message The string to process
     * @return The processed string
     */
    private static String deobfuscateLinks(String message) {
        return new MutableString(message)
                .replace("'", "")
                .replace("\"", "")
                .replace("()", ".")
                .replace("--", ".")
                .replace("<dot>", ".")
                .replace("[dot]", ".")
                .replace("{dot}", ".")
                .replace("(dot)", ".")
                .replace("<at>", ".")
                .replace("[at]", ".")
                .replace("{at}", ".")
                .replace("(at)", ".")
                .toString();
    }
}
