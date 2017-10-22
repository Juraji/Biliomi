package nl.juraji.biliomi.components.games.investments;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.InvestmentRecord;
import nl.juraji.biliomi.model.games.InvestmentSettings;
import nl.juraji.biliomi.model.games.UserInvestRecordStats;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class InvestmentGameComponent extends Component {

  @Inject
  private TimeFormatter timeFormatter;

  @Inject
  private PointsService pointsService;

  @Inject
  private InvestmentService investmentService;
  private InvestmentSettings settings;

  @Override
  public void init() {
    super.init();

    investmentService.start();
    settings = settingsService.getSettings(InvestmentSettings.class, s -> settings = s);
  }

  @CommandRoute(command = "pollmarket")
  public boolean pollMarketCommand(User user, Arguments arguments) {
    if (investmentService.isMarketStateGood()) {
      chat.say(i18n.get("ChatCommand.pollMarket.stateGood"));
    } else {
      chat.say(i18n.get("ChatCommand.pollMarket.stateBad"));
    }
    return true;
  }

  /**
   * Start an investment
   * Usage: !invest [amount] [interest percentage 0...100]
   */
  @CommandRoute(command = "invest", defaultCooldown = 900000)
  public boolean investCommand(User user, Arguments arguments) {
    Long investAmount = Numbers.asNumber(arguments.getSafe(0)).toLong();
    Integer investPercentage = Numbers.asNumber(arguments.getSafe(1)).toInteger();

    if (investAmount == null || investPercentage == null) {
      chat.whisper(user, i18n.get("ChatCommand.invest.usage"));
      return false;
    }

    if (user.getPoints() < investAmount) {
      chat.whisper(user, i18n.get("ChatCommand.invest.notEnoughPoints")
          .add("pointsname", pointsService::pointsName)
          .add("points", () -> pointsService.asString(user.getPoints())));
      return false;
    }

    double investPercentageReal = (double) investPercentage / 100;
    if (investPercentageReal < settings.getMinInterest() || investPercentageReal > settings.getMaxInterest()) {
      chat.whisper(user, i18n.get("ChatCommand.invest.usage.maximums")
          .add("time", timeFormatter.timeQuantity(settings.getInvestmentDuration()))
          .add("mininterest", MathUtils.doubleToPercentage(settings.getMinInterest()))
          .add("maxinterest", MathUtils.doubleToPercentage(settings.getMinInterest())));
      return false;
    }

    InvestmentRecord record = investmentService.startInvestment(user, investAmount, investPercentageReal);
    chat.say(i18n.get("ChatCommand.invest.invested")
        .add("username", user::getNameAndTitle)
        .add("project", record::getProject)
        .add("invested", () -> pointsService.asString(investAmount)));
    return true;
  }

  /**
   * State your current investment record status
   * Usage: !myinvestments
   */
  @CommandRoute(command = "myinvestments")
  public boolean myInvestmentsCommand(User user, Arguments arguments) {
    UserInvestRecordStats recordInfo = investmentService.getRecordInfo(user);

    if (recordInfo == null) {
      chat.whisper(user, i18n.get("ChatCommand.myinvestments.noRecords"));
      return false;
    }

    chat.say(i18n.get("ChatCommand.myinvestments.stat")
        .add("username", user::getNameAndTitle)
        .add("count", recordInfo::getRecordCount)
        .add("totalinvested", () -> pointsService.asString(recordInfo.getTotalInvested()))
        .add("totalearned", () -> pointsService.asString(recordInfo.getTotalEarned()))
        .add("successcount", recordInfo::getWins)
        .add("failedcount", recordInfo::getLosses)
        .add("comment", () -> i18n.getIfElse(recordInfo.isMoreWins(),
            "ChatCommand.myinvestments.stat.comment.mostlyWins",
            "ChatCommand.myinvestments.stat.comment.mostlyLosses")));
    return true;
  }

  /**
   * Manage investement settings
   * Usage: !investsettings [duration|mininterest|maxinterest] [more...]
   */
  @CommandRoute(command = "investsettings", systemCommand = true)
  public boolean investSettingsCommand(User user, Arguments arguments) {
    return captureSubCommands("investsettings", i18n.supply("ChatCommand.investsettings.usage"), user, arguments);
  }

  /**
   * Set the duration of an investment in minutes
   * Usage: !investsettings duration [time in minutes]
   */
  @SubCommandRoute(parentCommand = "investsettings", command = "duration")
  public boolean investSettingsCommandDuration(User user, Arguments arguments) {
    Integer durationMinutes = Numbers.asNumber(arguments.get(0)).toInteger();

    if (durationMinutes == null || durationMinutes < 0) {
      chat.whisper(user, i18n.get("ChatCommand.investsettings.duration.usage"));
      return false;
    }

    long duration = TimeUnit.MILLISECONDS.convert(durationMinutes, TimeUnit.MINUTES);

    settings.setInvestmentDuration(duration);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.investsettings.duration.set")
        .add("time", timeFormatter.timeQuantity(duration)));
    return true;
  }

  /**
   * Set the minimum interest rate for an investment
   * Usage: !investsettings mininterest [percentage 0...100]
   */
  @SubCommandRoute(parentCommand = "investsettings", command = "mininterest")
  public boolean investSettingsCommandMinInterest(User user, Arguments arguments) {
    Integer input = Numbers.asNumber(arguments.get(0)).toInteger();

    if (input == null || MathUtils.isNotInRange(input, 0, 100)) {
      chat.whisper(user, i18n.get("ChatCommand.investsettings.mininterest.usage"));
      return false;
    }

    double settingValue = input.doubleValue() / 100;
    if (settingValue > settings.getMaxInterest()) {
      chat.whisper(user, i18n.get("ChatCommand.investsettings.mininterest.higherThanMax")
          .add("percentage", MathUtils.doubleToPercentage(settings.getMaxInterest())));
    }

    settings.setMinInterest(settingValue);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.investsettings.mininterest.set")
        .add("percentage", MathUtils.doubleToPercentage(settingValue)));
    return true;
  }

  /**
   * Set the maximum interest rate for an investment
   * Usage: !investsettings maxinterest [percentage 0...100]
   */
  @SubCommandRoute(parentCommand = "investsettings", command = "maxinterest")
  public boolean investSettingsCommandMaxInterest(User user, Arguments arguments) {
    Integer input = Numbers.asNumber(arguments.get(0)).toInteger();

    if (input == null || MathUtils.isNotInRange(input, 0, 100)) {
      chat.whisper(user, i18n.get("ChatCommand.investsettings.maxinterest.usage"));
      return false;
    }

    double settingValue = input.doubleValue() / 100;
    if (settingValue < settings.getMinInterest()) {
      chat.whisper(user, i18n.get("ChatCommand.investsettings.mininterest.lowerThanMin")
          .add("percentage", MathUtils.doubleToPercentage(settings.getMinInterest())));
    }

    settings.setMaxInterest(settingValue);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.investsettings.maxinterest.set")
        .add("percentage", MathUtils.doubleToPercentage(settingValue)));
    return true;
  }
}
