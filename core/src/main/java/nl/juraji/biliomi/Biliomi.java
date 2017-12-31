package nl.juraji.biliomi;

import nl.juraji.biliomi.boot.SystemBoot;
import nl.juraji.biliomi.components.ComponentManager;
import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.rest.RestServerController;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;
import java.util.logging.Logger;

/**
 * Hello world!
 */
@Default
@Singleton
public class Biliomi implements Runnable {

  public void run() {
    CDI<Object> cdi = CDI.current();

    // Start console listener
    ConsoleApi consoleApi = cdi.select(ConsoleApi.class).get();
    consoleApi.init();

    // Run system boot
    SystemBoot systemBoot = cdi.select(SystemBoot.class).get();
    systemBoot.runSetupTasks();
    cdi.destroy(systemBoot);

    // Init ComponentManager
    cdi.select(ComponentManager.class).get();

    // Start IrcSession
    cdi.select(IrcSession.class).get().start();

    // Start REST API
    cdi.select(RestServerController.class).get().start();

    // Start listening for console commands
    consoleApi.initCliCommands();

    // The EventBusSubscriberInterceptor will register subscribers
    // Events are emitted by the IRC clients to bootstrap components and services
  }

  @PreDestroy
  public void destroyBiliomi() {
    Logger.getLogger(this.getClass().getName()).info("See you soon \u2764");
  }
}
