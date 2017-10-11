package nl.juraji.biliomi.io.console;

import nl.juraji.biliomi.io.console.support.ConsoleListener;
import nl.juraji.biliomi.io.console.support.LegacyConsoleListener;
import nl.juraji.biliomi.io.console.support.ModernConsoleListener;
import nl.juraji.biliomi.utility.commandrouters.cmd.CliCommandRouter;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.concurrent.DefaultThreadFactory;
import nl.juraji.biliomi.utility.types.Init;
import org.apache.logging.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutionException;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class ConsoleApi implements Init {

  private ConsoleListener consoleListener;

  @Inject
  private Logger logger;

  @Inject
  private EventBus eventBus;

  @Override
  public void init() {
    DefaultThreadFactory threadFactory = DefaultThreadFactory.newFactory("ConsoleApi", true);

    if (System.console() == null) {
      logger.info("Your system does not support console input via the Console API. Using legacy console input.");
      consoleListener = new LegacyConsoleListener(eventBus, logger);
    } else {
      consoleListener = new ModernConsoleListener(eventBus, logger);
    }

    threadFactory.newThread(consoleListener).start();
  }

  @PreDestroy
  private void destroy() {
    consoleListener.stop();
  }

  public void println() {
    println("");
  }

  public void println(String line) {
    System.out.println(line);
  }

  public void print(String line) {
    System.out.print(line);
  }

  public String awaitInput() throws ExecutionException, InterruptedException {
    return awaitInput(false);
  }

  /**
   * Await user input in the console
   *
   * @param requireInput If True the method will block until the user enters an non-null value
   * @return The user input
   */
  public String awaitInput(boolean requireInput) throws ExecutionException, InterruptedException {
    String input;

    do {
      input = consoleListener.nextInput().get();
    } while (requireInput && (input == null || input.length() == 0));

    return input;
  }

  /**
   * Await Y/N option from user
   * Requires input to match either y or n, ignoring case
   *
   * @return True if the user entered "y" or "Y" else false
   */
  public boolean awaitYesNo() throws ExecutionException, InterruptedException {
    String input;

    do {
      input = awaitInput(true);
    } while (!"Y".equalsIgnoreCase(input) && !"N".equalsIgnoreCase(input));

    return "Y".equalsIgnoreCase(input);
  }

  /**
   * Initialize the CLI command router
   */
  public void initCliCommands() {
    // Select the CliCommandrouter so it bootstraps
    CDI.current().select(CliCommandRouter.class).get();
    logger.debug("CLI command router initialized");
  }
}
