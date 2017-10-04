package nl.juraji.biliomi.io.console.support;

import nl.juraji.biliomi.utility.events.EventBus;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Juraji on 25-9-2017.
 * Biliomi
 */
public class LegacyConsoleListener extends ConsoleListener {

  private final BufferedReader reader;

  public LegacyConsoleListener(EventBus eventBus, Logger logger) {
    super(eventBus, logger);
    reader = new BufferedReader(new InputStreamReader(System.in));
  }

  @Override
  protected boolean isLegacy() {
    return true;
  }

  @Override
  protected String readLine() {
    String line = null;

    try {
      line = reader.readLine();
    } catch (IOException ignored) {
    }

    return line;
  }
}
