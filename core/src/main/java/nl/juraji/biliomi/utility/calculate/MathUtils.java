package nl.juraji.biliomi.utility.calculate;

import com.google.common.collect.Range;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class MathUtils {
  private static final SecureRandom RANDOM = new SecureRandom();

  private MathUtils() {
  }

  /**
   * Get a random between 0 and the max
   * (Includes min, Excludes max)
   *
   * @param max The maximum value to return
   * @return The random int generated
   */
  public static int rand(int max) {
    if (max > 0) {
      return RANDOM.nextInt(max);
    }
    return max;
  }

  /**
   * Get a random between 0 and the max
   * (Includes min, Excludes max)
   *
   * @param max The maximum value to return
   * @return The random long generated
   * @see <a href="https://commons.apache.org/proper/commons-math/javadocs/api-3.4/src-html/org/apache/commons/math3/random/RandomDataGenerator.html">org.apache.commons.math3.random.RandomDataGenerator</a>
   */
  public static long rand(long max) {
    if (max > 0) {
      final byte[] byteArray = new byte[8];
      long bits;
      long val;
      do {
        RANDOM.nextBytes(byteArray);
        bits = 0;
        for (final byte b : byteArray) {
          bits = (bits << 8) | (((long) b) & 0xffL);
        }
        bits &= 0x7fffffffffffffffL;
        val = bits % max;
      } while (bits - val + (max - 1) < 0);
      return val;
    }
    return max;
  }

  /**
   * Get a rancom between min and max
   * (Includes min, Includes max)
   *
   * @param min The minimum value
   * @param max The maximum value
   * @return The random int generated
   */
  public static int randRange(int min, int max) {
    return (int) randRange((long) min, max);
  }

  /**
   * Get a rancom between min and max
   * (Includes min, Includes max)
   *
   * @param min The minimum value
   * @param max The maximum value
   * @return The random long generated
   */
  public static long randRange(long min, long max) {
    if (min == (max + 1)) {
      return min;
    }
    return rand((max + 1) - min) + min;
  }

  /**
   * True or false, who knows
   *
   * @return True or false
   */
  public static boolean randChoice() {
    return rand(20) <= 10;
  }

  /**
   * Pick a random element in an array of objects
   *
   * @param array The array of objects
   * @param <T>   The object type
   * @return The chosen object
   */
  public static <T> T arrayRand(T[] array) {
    if (array.length == 0) {
      return null;
    }
    return array[rand(array.length)];
  }

  /**
   * Pick a random element in an list of objects
   *
   * @param list A list of objects
   * @param <T>  The object type
   * @return The chosen object
   */
  public static <T> T listRand(List<T> list) {
    if (list.size() == 0) {
      return null;
    }
    return list.get(rand(list.size()));
  }

  /**
   * Generate a new UUID using Java's UUID class
   * <p>
   * The generated UUID is described as cryptographically safe<br />
   * So it can be used in public identification
   * </p>
   *
   * @return A string containing the generated identifier
   */
  public static String newUUID() {
    return UUID.randomUUID().toString();
  }

  /**
   * Get the ordinal string of the given number (1st, 2nd, 3rd, 4th, ...)
   * Note: English only
   *
   * @param number the number to parse
   * @return the ordinal string
   */
  public static String getOrdinal(long number) {
    long mod100 = number % 100;
    long mod10 = number % 10;
    if (mod10 == 1 && mod100 != 11) {
      return number + "st";
    } else if (mod10 == 2 && mod100 != 12) {
      return number + "nd";
    } else if (mod10 == 3 && mod100 != 13) {
      return number + "rd";
    } else return number + "th";
  }

  /**
   * Calculate the percentage of current to max
   *
   * @param current the current value
   * @param max     the maximum value
   * @return The calculated percentage between 0 and 1
   */
  public static double calcPercentage(double current, double max) {
    return (current / max);
  }

  /**
   * Check of the given value is between the min and the mac value
   * (Switches min and max if min is larger than max)
   *
   * @param value The value to evaluate
   * @param min   The minimum value
   * @param max   The maximum value
   * @return true if the value is in the range else false
   */
  public static boolean isNotInRange(Number value, Number min, Number max) {
    double doubleMin = min.doubleValue();
    double doubleMax = max.doubleValue();
    double doubleValue = value.doubleValue();
    if (doubleMin > doubleMax) {
      return !Range.closed(doubleMax, doubleMin).contains(doubleValue);
    }
    return !Range.closed(doubleMin, doubleMax).contains(doubleValue);
  }

  /**
   * Convert a Double object to a percentage string
   * Beware that anything above 1 is more than a 100% e.g. 1.23 -> 123%
   *
   * @param number The double to convert
   * @return The resulting string e.g. 0.54 -> 54%
   */
  public static String doubleToPercentage(double number) {
    return (int) (number * 100) + "%";
  }

  /**
   * Calculate the greatest common denominator
   * <p>
   * Result:<br />
   * 1 == not dividable<br />
   * 2 == dividable
   * </p>
   *
   * @param a First number
   * @param b Second number
   * @return The greatest common denomitator
   */
  public static long getGcd(long a, long b) {
    return b == 0 ? a : getGcd(b, a % b);
  }

  /**
   * Convert two numbers to a simplified fraction
   *
   * @param a First number
   * @param b Second number
   * @return The fraction as a string e.g. 154, 308 -> "1/2"
   */
  public static String asFraction(long a, long b) {
    long gcm = getGcd(a, b);
    return (a / gcm) + "/" + (b / gcm);
  }

  /**
   * Force a value to be within the min/max bounds
   *
   * @param value The actual value
   * @param min   The minimum value
   * @param max   The maximum value
   * @return The value else min or max
   */
  public static int minMax(int value, int min, int max) {
    return (int) minMax((double) value, min, max);
  }

  /**
   * Force a value to be within the min/max bounds
   *
   * @param value The actual value
   * @param min   The minimum value
   * @param max   The maximum value
   * @return The value else min or max
   */
  public static long minMax(long value, long min, long max) {
    return (long) minMax((double) value, min, max);
  }

  /**
   * Force a value to be within the min/max bounds
   *
   * @param value The actual value
   * @param min   The minimum value
   * @param max   The maximum value
   * @return The value else min or max
   */
  public static double minMax(double value, double min, double max) {
    return (value < min ? min : (value > max ? max : value));
  }

  /**
   * Compare numbers to be equal
   *
   * @param numbers The numbers to compare
   * @return true if all numbers are equal, else false
   */
  public static boolean compareNumbers(Number... numbers) {
    return numbers.length != 0 && Arrays.stream(numbers).distinct().count() == 1;
  }

  /**
   * Compare numbers in grouped arrays to be equal.
   * Returns the first equality occurence!
   *
   * @param comparisons The groups of numbers to compare
   * @return 0 if none match, 1 or 2 or... for the respective argument offset that matched
   */
  public static int compareGroupedNumbers(Number[]... comparisons) {
    int pos = 0;
    for (Number[] comparison : comparisons) {
      ++pos;
      if (compareNumbers(comparison)) {
        return pos;
      }
    }
    return 0;
  }

  /**
   * Create an integer array
   *
   * @param integers The integers to put into the array
   * @return The array of Integers
   */
  public static Integer[] integerArray(Integer... integers) {
    return integers;
  }

  /**
   * Check wether a number is plural or not
   *
   * @param n The number to check
   * @return True if plural else false
   */
  public static boolean isPlural(Number n) {
    return (n == null || !n.equals(1));
  }
}
