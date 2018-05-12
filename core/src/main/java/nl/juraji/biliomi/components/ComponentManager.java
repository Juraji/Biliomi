package nl.juraji.biliomi.components;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.model.internal.events.irc.channel.IrcChannelJoinedEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.components.Component;
import org.apache.logging.log4j.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class ComponentManager {

  private boolean isInitialized = false;

  @Inject
  private Logger logger;

  @Inject
  private CommandRouter commandRouter;

  @Inject
  @SystemComponent
  private Instance<Component> systemComponents;

  @Inject
  @NormalComponent
  private Instance<Component> normalComponents;

  @PreDestroy
  public void destroyComponentManager() {
    normalComponents.forEach(normalComponents::destroy);
    systemComponents.forEach(systemComponents::destroy);
    isInitialized = false;
  }

  @Subscribe
  public void onIrcConnectedEvent(IrcChannelJoinedEvent event) {
    if (isInitialized) {
      logger.info("IRC connected, components were already initialized... So... moving on!");
    } else {
      isInitialized = true;
      logger.info("IRC connected, initializing components...");

      systemComponents.forEach(Component::init);
      normalComponents.forEach(Component::init);

      logger.info("All components loaded succesfully");
      logger.info("Biliomi is ready for commands!");
    }
  }

  @Subscribe
  public void onIrcChatMessageEvent(IrcChatMessageEvent event) {
    commandRouter.runCommand(event, false);
  }
}
