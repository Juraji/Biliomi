package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.components.interfaces.TimerService;
import nl.juraji.biliomi.utility.types.Templater;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 3-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class MessageTimerService extends TimerService {

  @Inject
  private ChatService chat;

  @PostConstruct
  private void initMessageTimerService() {
    this.start();
  }

  public void scheduleMessage(Templater messageTemplater, long delay, TimeUnit timeUnit) {
    schedule(() -> chat.say(messageTemplater), delay, timeUnit);
  }
}
