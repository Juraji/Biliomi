package nl.juraji.biliomi.boot.tasks;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.boot.SetupTaskPriority;
import nl.juraji.biliomi.io.api.twitch.oauth.TwitchOAuthFlowDirector;
import nl.juraji.biliomi.io.api.twitch.oauth.TwitchOAuthScope;
import nl.juraji.biliomi.io.console.ConsoleApi;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppDataValue;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.types.AppParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

/**
 * Created by Juraji on 27-7-2017.
 * Biliomi v3
 */
@Default
@SetupTaskPriority(priority = 3) // Fourth
public class TwitchOAuthTask implements SetupTask {

  @Inject
  private Logger logger;

  @Inject
  private ConsoleApi consoleApi;

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  @AppDataValue("twitch.api.uris.v5")
  private String twitchBaseUri;

  @Inject
  @UserSetting("biliomi.twitch.clientId")
  private String clientId;

  @Inject
  @ChannelName
  private String channelName;

  @Inject
  @BotName
  private String botName;

  @Override
  public void install() {
    try {
      AppParameters parameters = BiliomiContainer.getParameters();

      AuthToken channelToken = authTokenDao.get(TokenGroup.TWITCH, "channel");

      if (StringUtils.isEmpty(channelToken.getToken())) {
        String casterOAuthParam = parameters.getCasterOAuth();
        if (StringUtils.isEmpty(casterOAuthParam)) {
          logger.info("Install Twitch OAuth for " + channelName + " using OAuth flow...");
          installChannelAccessToken(channelToken);
        } else {
          logger.info("Install Twitch OAuth for " + channelName + " using --casteroauth parameter...");
          channelToken.setToken(casterOAuthParam);
        }

        authTokenDao.save(channelToken);
      }

      AuthToken botToken = authTokenDao.get(TokenGroup.TWITCH, "bot");

      if (StringUtils.isEmpty(botToken.getToken())) {
        String botOAuthParam = parameters.getBotOAuth();
        if (StringUtils.isEmpty(botOAuthParam)) {
          logger.info("Install Twitch OAuth for " + botName + " using OAuth flow...");
          installBotAccessToken(botToken);
        } else {
          logger.info("Install Twitch OAuth for " + botName + " using --botoauth parameter...");
          botToken.setToken(botOAuthParam);
        }

        authTokenDao.save(botToken);
      }
    } catch (Exception e) {
      logger.error("Failed installing Twitch OAuth tokens", e);
      BiliomiContainer.getContainer().shutdownInError();
    }
  }

  @Override
  public String getDisplayName() {
    return "Setup Twitch OAuth";
  }

  private void installChannelAccessToken(AuthToken oauthData) throws Exception {
    logger.info("Biliomi needs authorization to your channel's account, in order to fetch information on your channel and enable channel editing.");
    logger.info("Press [enter] to have biliomi open your browser to authenticate on Twitch with your channel's account...");

    consoleApi.awaitInput();
    installToken(oauthData::setToken,
        TwitchOAuthScope.CHAT_LOGIN,
        TwitchOAuthScope.CHANNEL_READ,
        TwitchOAuthScope.CHANNEL_EDITOR,
        TwitchOAuthScope.CHANNEL_SUBSCRIPTIONS);
  }

  private void installBotAccessToken(AuthToken oauthData) throws Exception {
    logger.info("Biliomi needs authorization to the bot's account, in order to be able to connect to the chat.");
    logger.info("Press [enter] to have biliomi open your browser to authenticate on Twitch with the bot's account...");

    consoleApi.awaitInput();
    installToken(oauthData::setToken, TwitchOAuthScope.CHAT_LOGIN);
  }

  private void installToken(Consumer<String> tokenSetter, TwitchOAuthScope... scopes) throws Exception {
    TwitchOAuthFlowDirector oAuthFlow = new TwitchOAuthFlowDirector(twitchBaseUri, clientId);
    String authenticationUrl = oAuthFlow.getAuthenticationUri(scopes);

    try {
      Desktop.getDesktop().browse(new URI(authenticationUrl));
      boolean authSuccess = oAuthFlow.awaitAccessToken();

      if (authSuccess) {
        tokenSetter.accept(oAuthFlow.getAccessToken());
        logger.info("Successfully installed Twitch OAuth token.");
      } else {
        throw new Exception(oAuthFlow.getAuthenticationError());
      }
    } catch (IOException e) {
      logger.error("You need to be able to open a web browser in order to authenticate with Twitch.");
      throw e;
    }
  }
}
