package nl.juraji.biliomi.components.system.users;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.SystemComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 28-4-2017.
 * Biliomi v3
 */
@Singleton
@SystemComponent
public class ModWatchComponent extends Component {

    @Inject
    private ModWatchTimerService modWatchTimer;

    @Override
    public void init() {
        modWatchTimer.start();
    }

    @CliCommandRoute(command = "updatemods", description = "Manually run the ModWatch")
    public boolean restartCommand(ConsoleInputEvent event) {
        modWatchTimer.updateNow();
        return true;
    }
}
