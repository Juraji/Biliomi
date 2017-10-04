package nl.juraji.biliomi.utility.events;

import com.google.common.eventbus.AsyncEventBus;
import nl.juraji.biliomi.utility.exceptions.EventSubscriberExceptionHandler;
import nl.juraji.biliomi.utility.factories.concurrent.ThreadPools;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class EventBus {
  private final ExecutorService executor = ThreadPools.newExecutorService(8, "EventBusExecutor");
  private final AsyncEventBus eventBus = new AsyncEventBus(executor, new EventSubscriberExceptionHandler());

  public void register(Object object) {
    eventBus.register(object);
  }

  public void unregister(Object object) {
    eventBus.unregister(object);
  }

  public void post(Event event) {
    eventBus.post(event);
  }

  @PreDestroy
  private void destroyEventBus() {
    executor.shutdown();
  }
}
