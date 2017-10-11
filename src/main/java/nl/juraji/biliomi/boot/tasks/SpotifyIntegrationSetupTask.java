package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.api.spotify.oauth.SpotifyOAuthFlowDirector;
import nl.juraji.biliomi.io.api.spotify.oauth.SpotifyOAuthScope;
import nl.juraji.biliomi.io.api.spotify.v1.SpotifyApi;
import nl.juraji.biliomi.io.api.spotify.v1.model.user.SpotifyUser;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

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
  private WebClient webClient;

  @Inject
  // Inject SpotifyApi as deferred inject, so it doesn't trigger any requests before token is installed
  private Instance<SpotifyApi> spotifyApiInstance;

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  @AppDataValue("spotify.api.uris.accounts")
  private String accountsBaseUri;

  @Inject
  @UserSetting("biliomi.integrations.spotify.consumerKey")
  private String consumerKey;

  @Inject
  @UserSetting("biliomi.integrations.spotify.consumerSecret")
  private String consumerSecret;

  @Override
  public void install() {
    if (StringUtils.isEmpty(consumerKey) || StringUtils.isEmpty(consumerSecret)) {
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

    SpotifyOAuthFlowDirector director = new SpotifyOAuthFlowDirector(accountsBaseUri, consumerKey, consumerSecret, webClient);
    String authenticationUrl = director.getAuthenticationUri(
        SpotifyOAuthScope.PLAYLIST_READ_PRIVATE,
        SpotifyOAuthScope.PLAYLIST_READ_COLLABORATIVE,
        SpotifyOAuthScope.PLAYLIST_MODIFY_PUBLIC,
        SpotifyOAuthScope.PLAYLIST_MODIFY_PRIVATE,
        SpotifyOAuthScope.USER_FOLLOW_READ,
        SpotifyOAuthScope.USER_LIBRARY_READ,
        SpotifyOAuthScope.USER_READ_PRIVATE,
        SpotifyOAuthScope.USER_READ_PLAYBACK_STATE,
        SpotifyOAuthScope.USER_READ_CURRENTLY_PLAYING);

    try {
      Desktop.getDesktop().browse(new URI(authenticationUrl));
      boolean authSuccess = director.awaitAccessToken();

      //noinspection Duplicates
      if (authSuccess) {
        token.setToken(director.getAccessToken());
        token.setRefreshToken(director.getRefreshToken());
        token.setIssuedAt(DateTime.now());
        token.setTimeToLive(director.getTimeToLive());
      } else {
        throw new Exception(director.getAuthenticationError());
      }

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
    } catch (IOException e) {
      throw new Exception("You need to be able to open a web browser in order to authenticate with Spotify.", e);
    }
  }
}
