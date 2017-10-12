package nl.juraji.biliomi.components.system.settings;

import com.google.common.base.Joiner;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.settings.SystemSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 28-4-2017.
 * Biliomi v3
 */
@SystemComponent
@Singleton
public class SystemSettingsComponent extends Component {

  @Inject
  @BotName
  private String botName;

  /**
   * Run a bot command as another user.
   * Biliomi will temporarily be muted, to prevent unwanted whispers.
   * After execution the mute will be turned off.
   * Usage: !execute [runas username] [command] [arguments...]
   */
  @CommandRoute(command = "execute", systemCommand = true)
  public boolean executeCommand(User user, Arguments arguments) {
    CommandRouter commandRouter = CDI.current().select(CommandRouter.class).get();

    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, l10n.get("ChatCommand.executeCommand.execute.usage"));
      return false;
    }

    String runAs = arguments.pop();
    User runAsUser = usersService.getUser(runAs);

    if (runAsUser == null) {
      chat.whisper(user, l10n.getUserNonExistent(runAs));
      return false;
    }

    OnOff originalMuteState = OnOff.fromBoolean(settingsService.getSystemSettings().isMuted());
    if (OnOff.OFF.equals(originalMuteState)) {
      muteCommand(user, new Arguments("mute", "on"));
    }

    String commandMessage = Joiner.on(" ").join(arguments);
    IrcChatMessageEvent event = new IrcChatMessageEvent(runAs, null, commandMessage);

    commandRouter.runCommand(event, false);

    if (OnOff.OFF.equals(originalMuteState)) {
      muteCommand(user, new Arguments("mute", "off"));
    }

    chat.whisper(user, l10n.get("ChatCommand.executeCommand.execute.done")
        .add("command", commandMessage)
        .add("user", runAsUser::getDisplayName));

    return true;
  }

  /**
   * Mute/Unmute Biliomi.
   * Omitting the on/off state will simply toggle the mute state.
   * Usage: !mute [on|off]
   */
  @CommandRoute(command = "mute", systemCommand = true)
  public boolean muteCommand(User user, Arguments arguments) {
    SystemSettings settings = settingsService.getSystemSettings();
    OnOff onOff = EnumUtils.toEnum(arguments.pop(), OnOff.class);


    // If on/off is supplied in the arguments, act acordingly
    if (OnOff.ON.equals(onOff)) {
      settings.setMuted(true);
    } else if (OnOff.OFF.equals(onOff)) {
      settings.setMuted(false);
    } else {
      // Else simply toggle
      settings.setMuted(!settings.isMuted());
    }

    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.muteCommand.toggleMute")
        .add("botname", botName)
        .add("state", l10n.getIfElse(settings.isMuted(), "ChatCommand.muteCommand.toggleMute.state.muted", "ChatCommand.muteCommand.toggleMute.state.unmuted")));
    return true;
  }

  /**
   * Set Biliomi's whisper mode.
   * Omitting the on/off state will simply toggle the whispermode state.
   * Usage: !whispermode [on|off]
   */
  @CommandRoute(command = "whispermode", systemCommand = true)
  public boolean whisperModeCommand(User user, Arguments arguments) {
    SystemSettings settings = settingsService.getSystemSettings();
    OnOff onOff = EnumUtils.toEnum(arguments.pop(), OnOff.class);

    // If on/off is supplied in the arguments, act acordingly
    if (OnOff.ON.equals(onOff)) {
      settings.setEnableWhispers(true);
    } else if (OnOff.OFF.equals(onOff)) {
      settings.setEnableWhispers(false);
    } else {
      // Else simply toggle
      settings.setEnableWhispers(!settings.isMuted());
    }

    settingsService.save(settings);
    chat.whisper(user, l10n.get("ChatCommand.toggleWhisperModeCommand.toggleWhisperMode")
        .add("state", l10n.getEnabledDisabled(settings.isMuted())));
    return true;
  }
}
