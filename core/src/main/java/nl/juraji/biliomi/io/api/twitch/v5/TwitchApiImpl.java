package nl.juraji.biliomi.io.api.twitch.v5;

import com.google.common.base.Joiner;
import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchGame;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.*;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class TwitchApiImpl implements TwitchApi {
  private static final int MAX_PAGE_SIZE = 100;
  private static final String OAUTH_HEADER_PREFIX = "OAuth ";
  private static final String API_BASE_URI = "https://api.twitch.tv/kraken";
  private static final String tmiBaseUri = "https://tmi.twitch.tv";

  @Inject
  @CoreSetting("biliomi.twitch.clientId")
  private String clientId;

  @Inject
  private WebClient webClient;

  @Inject
  private AuthTokenDao authTokenDao;

  private final HttpFields headers = new HttpFields();

  @PostConstruct
  public void initTwitchApi() {
    AuthToken casterToken = authTokenDao.get(TokenGroup.TWITCH, "channel");

    headers.put("Client-ID", clientId);
    headers.put(HttpHeader.ACCEPT, "application/vnd.twitchtv.v5+json");
    headers.put(HttpHeader.AUTHORIZATION, OAUTH_HEADER_PREFIX + casterToken.getToken());
  }

  @Override
  public Response<TwitchChannel> getChannel() throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channel"), headers, TwitchChannel.class);
  }

  @Override
  public Response<TwitchChannel> getChannel(long twitchId) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channels", twitchId), headers, TwitchChannel.class);
  }

  @Override
  public Response<TwitchChannel> updateChannel(long twitchId, String game, String status) throws Exception {
    TwitchChannelUpdate updateWrapper = new TwitchChannelUpdate();
    TwitchChannel updateData = new TwitchChannel();

    updateData.setGame(game);
    updateData.setStatus(status);
    updateWrapper.setChannel(updateData);

    String updateDataJson = JacksonMarshaller.marshal(updateWrapper);

    return webClient.put(Url.url(API_BASE_URI, "channels", twitchId), headers, updateDataJson, MediaType.JSON_UTF_8, TwitchChannel.class);
  }

  @Override
  public Response<TwitchFollows> getChannelFollowers(long twitchId, int limit, int offset) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channels", twitchId, "follows").withQuery(listQuery(limit, offset)), headers, TwitchFollows.class);
  }

  @Override
  public Response<TwitchSubscriptions> getChannelSubscriptions(long twitchId, int limit, int offset) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channels", twitchId, "subscriptions").withQuery(listQuery(limit, offset)), headers, TwitchSubscriptions.class);
  }

  @Override
  public Response<TwitchTeams> getChannelTeams(long twitchId) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channels", twitchId, "teams"), headers, TwitchTeams.class);
  }

  @Override
  public Response<TwitchCommunities> getChannelCommunities(long twitchId) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "channels", twitchId, "communities"), headers, TwitchCommunities.class);
  }

  @Override
  public Response<TwitchStreamInfo> getStream(long twitchId) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "streams", twitchId), headers, TwitchStreamInfo.class);
  }

  @Override
  public Response<TwitchUserLogins> getUsersByUsername(String... usernames) throws Exception {
    Map<String, Object> loginQuery = new HashMap<>();
    loginQuery.put("login", Joiner.on(",").join(usernames));
    return webClient.get(Url.url(API_BASE_URI, "users").withQuery(loginQuery), headers, TwitchUserLogins.class);
  }

  @Override
  public Response<TwitchUser> getUser(String twitchId) throws Exception {
    return webClient.get(Url.url(API_BASE_URI, "users", twitchId), headers, TwitchUser.class);
  }

  @Override
  public Response<TmiHosts> getHostUsers(String twitchId) throws Exception {
    Map<String, Object> query = new HashMap<>();
    query.put("include_logins", 1);
    query.put("target", twitchId);
    return webClient.get(Url.url(tmiBaseUri, "hosts").withQuery(query), null, TmiHosts.class);
  }

  @Override
  public TwitchGame searchGame(String gameName) throws Exception {
    Map<String, Object> query = new HashMap<>();
    query.put("query", gameName);

    Response<TwitchGames> response = webClient.get(Url.url(API_BASE_URI, "search", "games").withQuery(query), headers, TwitchGames.class);
    TwitchGame defaultGame = new TwitchGame();
    defaultGame.setName(gameName);

    if (response.isOK() && response.getData().getGames() != null) {
      return response.getData().getGames().stream()
          .filter(game -> StringUtils.equalsIgnoreCase(game.getName(), gameName))
          .findFirst()
          .orElse(defaultGame);
    }

    return defaultGame;
  }

  private Map<String, Object> listQuery(int limit, int offset) {
    Map<String, Object> query = new HashMap<>();
    query.put("limit", MathUtils.minMax(limit, 1, MAX_PAGE_SIZE));
    query.put("offset", offset);
    query.put("direction", "desc");
    return query;
  }
}
