package nl.juraji.biliomi.components.chat.usergreeting;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.model.internal.events.irc.user.state.IrcUserJoinedEvent;
import nl.juraji.biliomi.model.chat.UserGreeting;
import nl.juraji.biliomi.model.chat.UserGreetingDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.chat.settings.UserGreetingSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.shared.BadWordsService;
import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.TimedMap;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
@EventBusSubscriber
public class UserGreetingComponent extends Component {

  @Inject
  private UserGreetingDao userGreetingDao;

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private BadWordsService badWordsService;

  private final TimedMap<Long, Boolean> timeoutRegister = new TimedMap<>(getClass());
  private UserGreetingSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(UserGreetingSettings.class, s -> settings = s);
  }

  @Subscribe
  public void onIrcUserJoinedEvent(IrcUserJoinedEvent event) {
    User user = usersService.getUser(event.getUsername());
    if (user != null && !timeoutRegister.containsKey(user.getId())) {
      UserGreeting greeting = userGreetingDao.getByUser(user);

      if (greeting != null) {
        timeoutRegister.put(user.getId(), true, settings.getGreetingTimeout());
        chat.say(Templater.template(greeting.getMessage())
            .add("name", user::getNameAndTitle));
      }
    }
  }

  /**
   * Set your personal greeting
   * Use "{{name}}", without quotes, in your message to have it be replaced with your own name. Usage: !setgreeting [message... or OFF to disable]
   */
  @CommandRoute(command = "setgreeting")
  public boolean setgreetingCommand(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, l10n.get("ChatCommand.setGreeting.usage"));
      return false;
    }

    UserGreeting greeting = userGreetingDao.getByUser(user);

    OnOff off = EnumUtils.toEnum(arguments.get(0), OnOff.class);
    if (off != null) {
      if (OnOff.OFF.equals(off) && greeting != null) {
        userGreetingDao.delete(greeting);
        chat.whisper(user, l10n.get("ChatCommand.setGreeting.deleted"));
        return true;
      }
      return false;
    }

    String message = arguments.toString();
    if (badWordsService.containsBadWords(message)) {
      chat.whisper(user, l10n.getInputContainsBadWords());
      return false;
    }

    if (greeting == null) {
      greeting = new UserGreeting();
      greeting.setUser(user);
    }

    greeting.setMessage(message);
    userGreetingDao.save(greeting);

    chat.whisper(user, l10n.get("ChatCommand.setGreeting.saved"));
    return true;
  }

  /**
   * Update user greeting settings
   * Usage: !greetingsettings [enabled|timeout] [more...]
   */
  @CommandRoute(command = "greetingsettings", systemCommand = true)
  public boolean greetingSettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("greetingsettings", l10n.supply("ChatCommand.greetingSettings.usage"), user, arguments);
  }

  /**
   * Enable/disable user greetings
   * Usage: !greetingsettings enabled [on|off]
   */
  @SubCommandRoute(parentCommand = "greetingsettings", command = "enabled")
  public boolean greetingSettingsCommandEnabled(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.greetingSettings.enabled.usage"));
      return false;
    }

    settings.setEnableGreetings(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.greetingSettings.enabled.toggled")
        .add("state", l10n.getEnabledDisabled(settings.isEnableGreetings())));
    return true;
  }

  /**
   * Set the timeout for user greetings
   * Timouts will be reset when Biliomi restarts
   * Usage: !greetingsettings timeout [hours, 1 or more]
   */
  @SubCommandRoute(parentCommand = "greetingsettings", command = "timeout")
  public boolean greetingSettingsCommandTimeout(User user, Arguments arguments) {
    Integer timeoutHours = Numbers.asNumber(arguments.get(0)).toInteger();

    if (timeoutHours == null || timeoutHours < 1) {
      chat.whisper(user, l10n.get("ChatCommand.greetingSettings.timeout.usage"));
      return false;
    }

    long timeoutMillis = TimeUnit.MILLISECONDS.convert(timeoutHours, TimeUnit.HOURS);
    settings.setGreetingTimeout(timeoutHours);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("")
        .add("time", () -> timeFormatter.timeQuantity(timeoutMillis)));
    return true;
  }
}
