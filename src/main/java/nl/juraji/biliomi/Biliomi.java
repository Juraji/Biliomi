package nl.juraji.biliomi;

import nl.juraji.biliomi.boot.SystemBoot;
import nl.juraji.biliomi.io.api.twitch.pubsub.PubSubSession;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import nl.juraji.biliomi.components.ComponentManager;
import nl.juraji.biliomi.rest.RestServerController;
import twitter4j.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;

/**
 * Hello world!
 */
@Default
@Singleton
public class Biliomi implements Runnable {

  public void run() {
    CDI<Object> cdi = CDI.current();

    // Start console listener
    cdi.select(ConsoleApi.class).get().init();

    // Run system boot
    SystemBoot systemBoot = cdi.select(SystemBoot.class).get();
    systemBoot.runSetupTasks();
    cdi.destroy(systemBoot);

    // Init ComponentManager
    cdi.select(ComponentManager.class).get();

    // Start IrcSession
    cdi.select(IrcSession.class).get().start();

    // Start PubSubSession
    cdi.select(PubSubSession.class).get().start();

    // Start REST API
    cdi.select(RestServerController.class).get().start();

    // The EventBusSubscriberInterceptor will register subscribers
    // Events are emitted by the IRC clients to bootstrap components and services
  }

  @PreDestroy
  public void destroyBiliomi() {
    Logger.getLogger(this.getClass()).info("See you soon \u2764");
  }
}
