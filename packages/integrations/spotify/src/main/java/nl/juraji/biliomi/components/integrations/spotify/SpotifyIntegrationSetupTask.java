package nl.juraji.biliomi.components.integrations.spotify;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.components.integrations.spotify.api.oauth.SpotifyOAuthFlow;
import nl.juraji.biliomi.components.integrations.spotify.api.oauth.SpotifyOAuthScope;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.SpotifyApi;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.user.SpotifyUser;
import nl.juraji.biliomi.config.spotify.SpotifyConfigService;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Default
@SetupTaskPriority(priority = 10)
public class SpotifyIntegrationSetupTask implements SetupTask {

    @Inject
    private Logger logger;

    @Inject
    private ConsoleApi console;

    @Inject
    // Inject SpotifyApi as deferred inject, so it doesn't trigger any requests before token is installed
    private Instance<SpotifyApi> spotifyApiInstance;

    @Inject
    private AuthTokenDao authTokenDao;

    @Inject
    private SpotifyConfigService configService;

    @Override
    public void install() {
        if (StringUtils.isEmpty(configService.getConsumerKey()) || StringUtils.isEmpty(configService.getConsumerSecret())) {
            logger.info("No OAuth information set for Spotify, skipping set up");
            return;
        }

        try {
            AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "spotify");

            if (StringUtils.isEmpty(token.getToken())) {
                installAccessToken(token);
            }
        } catch (Exception e) {
            logger.error("Failed setting up Spotify integration", e);
        }
    }

    @Override
    public String getDisplayName() {
        return "Setup Spotify integration";
    }

    private void installAccessToken(AuthToken token) throws Exception {
        console.println();
        console.println("Biliomi can link to your spotify account to be able to present viewers with information like currently playing songs and integrate support for song requests using Spotify.");
        console.print("Would you like this? [y/n]: ");
        if (!console.awaitYesNo()) {
            logger.info("Skipping Spotify integration setup");
            return;
        }

        final SpotifyOAuthFlow flow = new SpotifyOAuthFlow(configService.getConsumerKey(), configService.getConsumerSecret());
        final SpotifyOAuthScope[] scopes = {
                SpotifyOAuthScope.PLAYLIST_READ_PRIVATE,
                SpotifyOAuthScope.PLAYLIST_READ_COLLABORATIVE,
                SpotifyOAuthScope.PLAYLIST_MODIFY_PUBLIC,
                SpotifyOAuthScope.PLAYLIST_MODIFY_PRIVATE,
                SpotifyOAuthScope.USER_FOLLOW_READ,
                SpotifyOAuthScope.USER_LIBRARY_READ,
                SpotifyOAuthScope.USER_READ_PRIVATE,
                SpotifyOAuthScope.USER_READ_PLAYBACK_STATE,
                SpotifyOAuthScope.USER_READ_CURRENTLY_PLAYING
        };

        flow.installAccessToken(token, SpotifyOAuthScope.join(scopes));

        authTokenDao.save(token);
        // Get spotifyApi instance now via CDI, so it doesn't try to trigger using tokens that are not set yet
        SpotifyApi spotifyApi = spotifyApiInstance.get();
        Response<SpotifyUser> me = spotifyApi.getMe();

        if (!me.isOK()) {
            // Delete the former persisted token
            authTokenDao.delete(token);
            throw new Exception("Could not retrieve user id");
        }

        token.setUserId(me.getData().getId());
        authTokenDao.save(token);
        console.println("Successfully set up Spotfy integration for user " + me.getData().getDisplayName() + ".");
        console.println();
    }
}
