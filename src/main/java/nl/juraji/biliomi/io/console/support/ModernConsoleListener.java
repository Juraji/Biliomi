package nl.juraji.biliomi.io.console.support;

import nl.juraji.biliomi.utility.events.EventBus;
import org.apache.logging.log4j.Logger;

import java.io.Console;

/**
 * Created by Juraji on 25-9-2017.
 * Biliomi
 */
public class ModernConsoleListener extends ConsoleListener {

  private final Console console;

  public ModernConsoleListener(EventBus eventBus, Logger logger) {
    super(eventBus, logger);
    console = System.console();
  }

  @Override
  protected boolean isLegacy() {
    return false;
  }

  @Override
  protected String readLine() {
    return console.readLine();
  }
}
