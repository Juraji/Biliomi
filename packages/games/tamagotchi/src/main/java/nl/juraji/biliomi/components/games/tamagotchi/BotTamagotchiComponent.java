package nl.juraji.biliomi.components.games.tamagotchi;

import nl.juraji.biliomi.components.games.tamagotchi.services.TamagotchiService;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;
import nl.juraji.biliomi.model.games.settings.TamagotchiSettings;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;
import nl.juraji.biliomi.utility.types.enums.OnOff;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by robin on 1-6-17.
 * biliomi
 */
@Default
@Singleton
@NormalComponent
public class BotTamagotchiComponent extends Component {
    private static final String BOT_TG_NAME = "Tara";
    private static final String BOT_TG_SPECIES = "robodog";

    @Inject
    @BotName
    private String botName;

    @Inject
    private TamagotchiService tamagotchiService;

    private TamagotchiSettings settings;

    @Override
    public void init() {
        settings = settingsService.getSettings(TamagotchiSettings.class, s -> settings = s);

        // Create the bot tamagotchi, if it's enabled, but does't exist yet. Mostly on first boot
        if (settings.isBotTamagotchiEnabled()) {
            createBotTGIfNotExists();
        } else {
            killBotTgIfExists();
        }
    }

    /**
     * Manage the bot's tamagotchi
     * Usage: !bottamagotchi [enabled] [more...]
     */
    @CommandRoute(command = "bottamagotchi", systemCommand = true)
    public boolean botTamagotchiCommand(User user, Arguments arguments) {
        return captureSubCommands("bottamagotchi", i18n.supply("ChatCommand.botTamagotchi.usage"), user, arguments);
    }

    /**
     * Enable/disable the bot's tamagotchi
     * Usage: !bottamagotchi enabled [on|off]
     */
    @SubCommandRoute(parentCommand = "bottamagotchi", command = "enabled")
    public boolean botTamagotchiCommandEnabled(User user, Arguments arguments) {
        OnOff onOff = EnumUtils.toEnum(arguments.get(0), OnOff.class);

        if (onOff == null) {
            chat.whisper(user, i18n.get("ChatCommand.botTamagotchi.enabled.usage"));
            return false;
        }

        settings.setBotTamagotchiEnabled(OnOff.ON.equals(onOff));
        settingsService.save(settings);

        if (settings.isBotTamagotchiEnabled()) {
            // Create a new Tamagotchi if it hasn't been enabled yet
            createBotTGIfNotExists();
        } else {
            // Kill the current tamagotchi if it exists
            killBotTgIfExists();
        }

        chat.whisper(user, i18n.get("ChatCommand.botTamagotchi.enabled.toggled")
                .add("state", i18n.getEnabledDisabled(settings.isBotTamagotchiEnabled())));

        return true;
    }

    private void createBotTGIfNotExists() {
        User botUser = usersService.getUser(botName);
        if (!tamagotchiService.userHasTamagotchi(botUser)) {
            tamagotchiService.createTamagotchi(BOT_TG_NAME, botUser, BOT_TG_SPECIES);
        }
    }

    private void killBotTgIfExists() {
        User botUser = usersService.getUser(botName);
        Tamagotchi tamagotchi = tamagotchiService.getTamagotchi(botUser);
        if (tamagotchi != null) {
            tamagotchiService.kill(tamagotchi);
            tamagotchiService.save(tamagotchi);
        }
    }
}
