package nl.juraji.biliomi.io.console;

/**
 * Created by Juraji on 14-5-2017.
 * Biliomi v3
 */
@SuppressWarnings("unused")
public final class Ansi {
  private static final String ANSI_ESCAPE_PREFIX = "\u001B";
  private static final String RESET = ANSI_ESCAPE_PREFIX + "[0m";
  private static final String RED = ANSI_ESCAPE_PREFIX + "[31m";
  private static final String GREEN = ANSI_ESCAPE_PREFIX + "[32m";
  private static final String YELLOW = ANSI_ESCAPE_PREFIX + "[33m";
  private static final String BLUE = ANSI_ESCAPE_PREFIX + "[34m";
  private static final String PURPLE = ANSI_ESCAPE_PREFIX + "[35m";
  private static final String CYAN = ANSI_ESCAPE_PREFIX + "[36m";

  private Ansi() {
    // Private constructor
  }

  public static String red(Object o) {
    return format(o, RED);
  }

  public static String green(Object o) {
    return format(o, GREEN);
  }

  public static String yellow(Object o) {
    return format(o, YELLOW);
  }

  public static String blue(Object o) {
    return format(o, BLUE);
  }

  public static String purple(Object o) {
    return format(o, PURPLE);
  }

  public static String cyan(Object o) {
    return format(o, CYAN);
  }

  private static String format(Object o, String color) {
    return color + String.valueOf(o) + RESET;
  }
}
