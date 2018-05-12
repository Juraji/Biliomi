package nl.juraji.biliomi.components.integrations.steam.setup;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.net.URI;

/**
 * Created by Juraji on 15-9-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 10)
public class SteamIntegrationSetupTask implements SetupTask {

    @Inject
    private Logger logger;

    @Inject
    private ConsoleApi console;

    @Inject
    private AuthTokenDao authTokenDao;
    private AuthToken userToken;

    @Override
    public void install() {
        userToken = authTokenDao.get(TokenGroup.INTEGRATIONS, "steam");

        if (userToken.getToken() == null) {
            try {
                installSteamToken();
            } catch (Exception e) {
                logger.error("Failed setting up Steam integration", e);
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Setup Steam integration";
    }

    private void installSteamToken() throws Exception {
        console.println();
        console.print("Would you like to set up Steam integration now? [y/n]: ");
        if (!console.awaitYesNo()) {
            console.println();
            logger.info("Canceled Steam integration setup");
            return;
        }

        console.println();
        console.println("You will need a Steam Api key.");
        console.print("Hit [enter] to open up http://steamcommunity.com/dev/apikey fill out the form.");
        Desktop.getDesktop().browse(new URI("http://steamcommunity.com/dev/apikey"));

        console.println();
        console.print("Enter your Steam API key and hit [enter]: ");
        String apiKey = console.awaitInput(true);
        userToken.setToken(apiKey);

        console.println();
        console.println("Note: You can use https://steamidfinder.com/ to find out your user id.");
        console.print("Enter your Steam user id and hit [enter]: ");
        String userId = console.awaitInput(true);
        userToken.setUserId(userId);

        authTokenDao.save(userToken);
        console.println("Successfully set up Steam integration");
        console.println();
    }
}
