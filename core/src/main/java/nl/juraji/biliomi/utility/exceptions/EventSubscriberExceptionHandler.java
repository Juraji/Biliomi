package nl.juraji.biliomi.utility.exceptions;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Juraji on 23-4-2017.
 * Biliomi v3
 */
public final class EventSubscriberExceptionHandler implements SubscriberExceptionHandler {

  @Override
  public void handleException(Throwable throwable, SubscriberExceptionContext subscriberExceptionContext) {
    String eventName = subscriberExceptionContext.getEvent().getClass().getSimpleName();
    Class<?> subscriber = subscriberExceptionContext.getSubscriber().getClass();

    LogManager.getLogger(subscriber).error("An error occurred on event " + eventName + " in " + subscriber.getName(), throwable);
  }
}
