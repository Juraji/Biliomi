package nl.juraji.biliomi.components.interfaces;

import nl.juraji.biliomi.utility.calculate.WeldUtils;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;
import nl.juraji.biliomi.utility.types.Restartable;
import org.apache.logging.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
public abstract class TimerService implements Restartable {

  @Inject
  protected Logger logger;

  private ScheduledExecutorService timerExecutor;

  @Override
  public void start() {
    if (timerExecutor == null) {
      timerExecutor = ThreadPools.newScheduledExecutorService(WeldUtils.getAbsoluteClass(this).getSimpleName());
    }
  }

  @PreDestroy
  @Override
  public void stop() {
    try {
      if (timerExecutor != null) {
        timerExecutor.shutdownNow();
        timerExecutor.awaitTermination(5, TimeUnit.SECONDS);
      }
    } catch (InterruptedException e) {
      // An interuption occurred, but that's what we're going for anyway
      Thread.currentThread().interrupt();
    } finally {
      timerExecutor = null;
    }
  }

  protected void scheduleAtFixedRate(Runnable command, long period, TimeUnit unit) {
    scheduleAtFixedRate(command, period, period, unit);
  }

  protected void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    timerExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  protected void schedule(Runnable command, long delay, TimeUnit unit) {
    timerExecutor.schedule(command, delay, unit);
  }
}
