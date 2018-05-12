package nl.juraji.biliomi.utility.calculate;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TextUtils {
    private static final String COMMA_LIST_SEPARATOR = ", ";

    private TextUtils() {
    }

    /**
     * Chunkify strings to a set length, but do not break words
     *
     * @param string The string to chunkify
     * @param length The max length of the chunks
     * @return A list containing the resulting chunks
     */
    public static List<String> chunkify(String string, int length) {
        List<String> chunks = new ArrayList<>();

        if (StringUtils.isEmpty(string) || string.length() <= length) {
            chunks.add(string);
            return chunks;
        }

        Iterator<String> words = Iterators.forArray(string.split(" "));
        int chunkCount = (int) Math.ceil(((double) string.length()) / length);

        IntStream.rangeClosed(1, chunkCount).forEach(ci -> {
            StringBuilder newChunk = new StringBuilder();

            while (newChunk.length() < length) {
                if (!words.hasNext()) break;
                newChunk.append(" ").append(words.next());
            }

            if (StringUtils.isNotEmpty(newChunk)) chunks.add(newChunk.toString().trim());
        });

        return chunks;
    }

    /**
     * Merge a collection of strings separating each item with a comma
     *
     * @param collection The collection to merge
     * @return The resulting string. May be empty if the collection was empty or null
     */
    @NotNull
    public static String commaList(Collection<String> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        return commaList(collection.stream());
    }

    /**
     * Merge a stream of strings separating each item with a comma
     *
     * @param stream The stream to merge
     * @return The resulting string. May be empty if the stream was empty
     */
    public static String commaList(Stream<String> stream) {
        return stream
                .reduce((l, r) -> l + COMMA_LIST_SEPARATOR + r)
                .orElse("");
    }
}
