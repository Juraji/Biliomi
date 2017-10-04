package nl.juraji.biliomi.components.chat.bits;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.chat.settings.BitsSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 10-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class BitsComponent extends Component {

  @Inject
  private SettingsService settingsService;

  @Inject
  private BitsComponentEventService bitsComponentEventService;

  @Override
  public void init() {
    bitsComponentEventService.init();
  }

  /**
   * The main command for setting up whether Biliomi should convert cheered bits to points
   * for the cheering user
   * Usage: !bitstopoints [enable|tochatters|multiplier] [more...]
   */
  @CommandRoute(command = "bitstopoints", systemCommand = true)
  public boolean bitsToPointsCommmand(User user, Arguments arguments) {
    return captureSubCommands("bitstopoints", l10n.supply("ChatCommand.bitsToPoints.usage"), user, arguments);
  }

  /**
   * Enable/Disable converting cheered bits to points for the cheering user
   * Usage: !bitstopoints enable [on or off]
   */
  @SubCommandRoute(parentCommand = "bitstopoints", command = "enable")
  public boolean bitsToPointsCommmandEnable(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.enable.usage"));
      return false;
    }

    BitsSettings settings = settingsService.getSettings(BitsSettings.class);
    settings.setEnableBitsToPoints(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.enable.set")
        .add("state", l10n.getEnabledDisabled(settings.isEnableBitsToPoints())));
    return true;
  }

  /**
   * Should Biliomi also give converted points to all the other chatters?
   * Usage: !bitstopoints tochatters [on or off]
   */
  @SubCommandRoute(parentCommand = "bitstopoints", command = "tochatters")
  public boolean bitsToPointsCommmandToChatters(User user, Arguments arguments) {
    OnOff onOff = EnumUtils.toEnum(arguments.getSafe(0), OnOff.class);

    if (onOff == null) {
      chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.toChatters.usage"));
      return false;
    }

    BitsSettings settings = settingsService.getSettings(BitsSettings.class);
    settings.setBitsToPointsPayoutToAllChatters(OnOff.ON.equals(onOff));
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.toChatters.set")
        .add("state", l10n.getEnabledDisabled(settings.isBitsToPointsPayoutToAllChatters())));
    return true;
  }

  /**
   * Set the multiplier for converting bits to points
   * Also supports division by using a number like 0.5 for splitting in half
   * Usage: !bitstopoints multiplier [number higher than 0]
   */
  @SubCommandRoute(parentCommand = "bitstopoints", command = "multiplier")
  public boolean bitsToPointsCommmandMultiplier(User user, Arguments arguments) {
    Double newMultiplier = Numbers.asNumber(arguments.getSafe(0)).toDouble();

    if (newMultiplier == null || newMultiplier <= 0) {
      chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.multiplier.usage"));
      return false;
    }

    BitsSettings settings = settingsService.getSettings(BitsSettings.class);
    settings.setBitsToPointsMultiplier(newMultiplier);
    settingsService.save(settings);

    chat.whisper(user, l10n.get("ChatCommand.bitsToPoints.multiplier.set")
        .add("amount", newMultiplier));
    return true;
  }
}
