package nl.juraji.biliomi.io.console.support;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.events.EventBus;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Created by Juraji on 25-9-2017.
 * Biliomi
 */
public abstract class ConsoleListener implements Runnable {
  private static final int THREAD_SLEEP = 500;
  private final EventBus eventBus;
  private final Logger logger;
  private boolean active;
  private CompletableFuture<String> nextInputFuture;

  protected ConsoleListener(EventBus eventBus, Logger logger) {
    this.eventBus = eventBus;
    this.logger = logger;
  }

  @Override
  public void run() {
    active = true;

    while (active) {
      try {
        String s = readLine();

        if (nextInputFuture != null) {
          nextInputFuture.complete(s);
          nextInputFuture = null;
        }

        if (s != null) {
          eventBus.post(new ConsoleInputEvent(s, isLegacy()));
        }

        Thread.sleep(THREAD_SLEEP);
      } catch (InterruptedException e) {
        logger.error("An error occurred reading console input", e);
        Thread.currentThread().interrupt();
      }
    }
  }

  public Future<String> nextInput() {
    nextInputFuture = new CompletableFuture<>();
    return nextInputFuture;
  }

  protected abstract boolean isLegacy();

  protected abstract String readLine();

  public void stop() {
    this.active = false;
  }
}
