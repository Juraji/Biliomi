package nl.juraji.biliomi.components.games.roulette;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.config.roulette.RouletteConfigService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.RouletteSettings;
import nl.juraji.biliomi.model.games.UserRecordStats;
import nl.juraji.biliomi.model.internal.events.bot.AchievementEvent;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class RouletteComponent extends Component {

    @Inject
    private RouletteRecordService rouletteRecordService;

    @Inject
    private RouletteConfigService configService;

    @Inject
    private TimeFormatter timeFormatter;

    private RouletteSettings settings;

    @Override
    public void init() {
        settings = settingsService.getSettings(RouletteSettings.class, s -> settings = s);
    }

    /**
     * Play the roulette game
     * Usage: !roulette
     */
    @CommandRoute(command = "roulette")
    public boolean rouletteCommand(User user, Arguments arguments) {
        boolean isFatal = MathUtils.randChoice();
        String message;

        if (isFatal) {
            message = configService.getRandomLost();

            if (settings.isTimeoutOnDeathEnabled() && !user.isCaster() && !user.isModerator()) {
                chat.timeoutUser(user.getUsername(), settings.getTimeoutOnDeath());
                chat.whisper(user, i18n.get("ChatCommand.roulette.timoutNotice")
                        .add("time", timeFormatter.timeQuantity(settings.getTimeoutOnDeath())));
            }
        } else {
            eventBus.post(new AchievementEvent(user, "RO_QUICKSILVER", i18n.getString("Achievement.quickSilver")));
            message = configService.getRandomWin();
        }

        chat.say(Templater.template(message)
                .add("username", user::getDisplayName));
        rouletteRecordService.recordRouletteRun(user, isFatal);

        return true;
    }

    /**
     * State your current record on the Roulette game
     * Usage: !myrouletterecord
     */
    @CommandRoute(command = "myrouletterecord")
    public boolean myrouletterecordCommand(User user, Arguments arguments) {
        UserRecordStats recordInfo = rouletteRecordService.getRecordInfo(user);

        if (recordInfo.getRecordCount() == 0) {
            chat.whisper(user, i18n.get("ChatCommand.myrouletterecord.noRecords"));
        } else {
            chat.say(i18n.get("ChatCommand.myrouletterecord.stat")
                    .add("username", user::getNameAndTitle)
                    .add("totalcount", recordInfo::getRecordCount)
                    .add("wincount", recordInfo::getWins)
                    .add("lostcount", recordInfo::getLosses));
        }
        return true;
    }

    /**
     * Manage roulette settings
     * Usage: !roulettesettings [timeoutenabled|timeoutondeath] [more...]
     */
    @CommandRoute(command = "roulettesettings", systemCommand = true)
    public boolean roulettesettingsCommand(User user, Arguments arguments) {
        return captureSubCommands("roulettesettings", i18n.supply("ChatCommand.roulettesettings.usage"), user, arguments);
    }

    /**
     * Enable/disable user timeout on roulettedeath
     * Usage: !roulettesettings timeoutenabled [on|off]
     */
    @SubCommandRoute(parentCommand = "roulettesettings", command = "timeoutenabled")
    public boolean roulettesettingsCommandTimoutEnabled(User user, Arguments arguments) {
        OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

        if (onOff == null) {
            chat.whisper(user, i18n.get("ChatCommand.roulettesettings.timoutEnabled.usage"));
            return false;
        }

        settings.setTimeoutOnDeathEnabled(OnOff.ON.equals(onOff));
        settingsService.save(settings);

        chat.whisper(user, i18n.get("ChatCommand.roulettesettings.timoutOnDeathOrTimoutEnabled.set")
                .add("time", timeFormatter.timeQuantity(settings.getTimeoutOnDeath()))
                .add("state", i18n.getEnabledDisabled(settings.isTimeoutOnDeathEnabled())));
        return true;
    }

    /**
     * Set the timout on death
     * Note: Caster and moderators won't get timed out, since they can't be
     * Usage: !roulettesettings timeoutondeath [time in minutes]
     */
    @SubCommandRoute(parentCommand = "roulettesettings", command = "timeoutondeath")
    public boolean roulettesettingsCommandTimoutOnDeath(User user, Arguments arguments) {
        Integer timeoutMinutes = NumberConverter.asNumber(arguments.get(0)).toInteger();

        if (timeoutMinutes == null) {
            chat.whisper(user, i18n.get("ChatCommand.roulettesettings.timoutOnDeath.usage"));
            return false;
        }

        long timeoutMillis = TimeUnit.MILLISECONDS.convert(timeoutMinutes, TimeUnit.MINUTES);
        settings.setTimeoutOnDeath(timeoutMillis);
        settingsService.save(settings);

        chat.whisper(user, i18n.get("ChatCommand.roulettesettings.timoutOnDeathOrTimoutEnabled.set")
                .add("time", timeFormatter.timeQuantity(settings.getTimeoutOnDeath()))
                .add("state", i18n.getEnabledDisabled(settings.isTimeoutOnDeathEnabled())));
        return true;
    }
}
