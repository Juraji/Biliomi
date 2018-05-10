package nl.juraji.biliomi.components.integrations.streamlabs;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth.StreamLabsOAuthFlow;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth.StreamLabsOAuthScope;
import nl.juraji.biliomi.config.spotify.StreamLabsConfigService;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 10)
public class StreamLabsIntegrationSetupTask implements SetupTask {

    @Inject
    private Logger logger;

    @Inject
    private ConsoleApi console;

    @Inject
    private AuthTokenDao authTokenDao;

    @Inject
    private StreamLabsConfigService configService;

    @Override
    public void install() {
        if (StringUtils.isEmpty(configService.getConsumerKey()) || StringUtils.isEmpty(configService.getConsumerSecret())) {
            logger.info("No OAuth information set for Stream Labs, skipping set up");
            return;
        }

        try {
            AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "streamlabs");

            if (StringUtils.isEmpty(token.getToken())) {
                installAccessToken(token);
            }
        } catch (Exception e) {
            logger.error("Failed setting up Stream Labs integration", e);
        }
    }

    @Override
    public String getDisplayName() {
        return "Setup Stream Labs integration";
    }

    private void installAccessToken(AuthToken token) throws Exception {
        console.println();
        console.println("Biliomi can link to your Stream Labs account and watch various stats like donations, hosts etc.");
        console.print("Would you like this? [y/n]: ");

        if (!console.awaitYesNo()) {
            logger.info("Skipping Stream Labs integration setup");
            return;
        }

        StreamLabsOAuthFlow flow = new StreamLabsOAuthFlow(configService.getConsumerKey(), configService.getConsumerSecret());
        final StreamLabsOAuthScope[] scopes = {
                StreamLabsOAuthScope.DONATIONS_READ,
                StreamLabsOAuthScope.SOCKET_TOKEN
        };

        flow.installAccessToken(token, StreamLabsOAuthScope.join(scopes));
        authTokenDao.save(token);
    }
}
