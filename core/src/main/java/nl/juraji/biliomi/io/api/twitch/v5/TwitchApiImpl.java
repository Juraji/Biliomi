package nl.juraji.biliomi.io.api.twitch.v5;

import com.google.common.base.Joiner;
import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.api.twitch.oauth.TwitchOAuthFlow;
import nl.juraji.biliomi.io.api.twitch.v5.model.*;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.*;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.Community;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
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
import java.util.Set;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class TwitchApiImpl implements TwitchApi {
    private static final int MAX_PAGE_SIZE = 100;
    private final HttpFields headers = new HttpFields();
    @Inject
    @AppData("apis.twitch.v5.baseUri")
    private String apiBaseUri;
    @Inject
    @AppData("apis.twitch.tmi.baseUri")
    private String tmiBaseUri;
    @Inject
    @CoreSetting("biliomi.twitch.clientId")
    private String clientId;
    @Inject
    @CoreSetting("biliomi.twitch.clientSecret")
    private String clientSecret;
    @Inject
    private WebClient webClient;
    @Inject
    private AuthTokenDao authTokenDao;
    private TwitchOAuthFlow twitchOAuthFlow;

    @PostConstruct
    public void initTwitchApi() {
        AuthToken casterToken = authTokenDao.get(TokenGroup.TWITCH, "channel");

        headers.put("Client-ID", clientId);
        headers.put(HttpHeader.ACCEPT, "application/vnd.twitchtv.v5+json");
        headers.put(HttpHeader.AUTHORIZATION, "OAuth " + casterToken.getToken());

        twitchOAuthFlow = new TwitchOAuthFlow(clientId, clientSecret);
    }

    @Override
    public Response<TwitchChannel> getChannel() throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "channel"), headers, TwitchChannel.class);
    }

    @Override
    public Response<TwitchChannel> getChannel(long twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "channels", twitchId), headers, TwitchChannel.class);
    }

    @Override
    public Response<TwitchChannel> updateChannel(long twitchId, String game, String status) throws Exception {
        validateOrRefreshToken();

        TwitchChannelUpdate updateWrapper = new TwitchChannelUpdate();
        TwitchChannel updateData = new TwitchChannel();

        updateData.setGame(game);
        updateData.setStatus(status);
        updateWrapper.setChannel(updateData);

        String updateDataJson = JacksonMarshaller.marshal(updateWrapper);

        return webClient.put(Url.url(apiBaseUri, "channels", twitchId), headers, updateDataJson, MediaType.JSON_UTF_8, TwitchChannel.class);
    }

    @Override
    public Response<TwitchFollows> getChannelFollowers(long twitchId, int limit, int offset) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(apiBaseUri, "channels", twitchId, "follows")
                .withQueryParams(listQuery(limit, offset));
        return webClient.get(url, headers, TwitchFollows.class);
    }

    @Override
    public Response<TwitchSubscriptions> getChannelSubscriptions(long twitchId, int limit, int offset) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(apiBaseUri, "channels", twitchId, "subscriptions")
                .withQueryParams(listQuery(limit, offset));
        return webClient.get(url, headers, TwitchSubscriptions.class);
    }

    @Override
    public Response<TwitchTeams> getChannelTeams(long twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "channels", twitchId, "teams"), headers, TwitchTeams.class);
    }

    @Override
    public Response<TwitchCommunity> getCommunityByName(String communityName) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(apiBaseUri, "communities")
                .withQueryParam("name", communityName);
        return webClient.get(url, headers, TwitchCommunity.class);
    }

    @Override
    public Response<TwitchCommunities> getChannelCommunities(long twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "channels", twitchId, "communities"), headers, TwitchCommunities.class);
    }

    @Override
    public Response<Void> updateChannelCommunities(long twitchId, Set<Community> communities) throws Exception {
        validateOrRefreshToken();

        TwitchCommunitiesUpdate update = new TwitchCommunitiesUpdate();

        communities.stream()
                .limit(3)
                .map(Community::getTwitchId)
                .forEach(id -> update.getCommunityIds().add(id));

        String body = JacksonMarshaller.marshal(update);

        return webClient.put(Url.url(apiBaseUri, "channels", twitchId, "communities"), headers, body, MediaType.JSON_UTF_8, Void.class);
    }

    @Override
    public Response<Void> clearChannelCommunities(long twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.delete(Url.url(apiBaseUri, "channels", twitchId, "community"), headers, Void.class);
    }

    @Override
    public Response<TwitchStreamInfo> getStream(long twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "streams", twitchId), headers, TwitchStreamInfo.class);
    }

    @Override
    public Response<TwitchUserLogins> getUsersByUsername(String... usernames) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(apiBaseUri, "users")
                .withQueryParam("login", Joiner.on(",").join(usernames));
        return webClient.get(url, headers, TwitchUserLogins.class);
    }

    @Override
    public Response<TwitchUser> getUser(String twitchId) throws Exception {
        validateOrRefreshToken();

        return webClient.get(Url.url(apiBaseUri, "users", twitchId), headers, TwitchUser.class);
    }

    @Override
    public Response<TmiHosts> getHostUsers(String twitchId) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(tmiBaseUri, "hosts")
                .withQueryParam("include_logins", 1)
                .withQueryParam("target", twitchId);
        return webClient.get(url, null, TmiHosts.class);
    }

    @Override
    public TwitchGame searchGame(String gameName) throws Exception {
        validateOrRefreshToken();

        Url url = Url.url(apiBaseUri, "search", "games")
                .withQueryParam("query", gameName);

        Response<TwitchGames> response = webClient.get(url, headers, TwitchGames.class);
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

    private synchronized void validateOrRefreshToken() throws Exception {
        AuthToken casterToken = authTokenDao.get(TokenGroup.TWITCH, "channel");
        final boolean tokenValid = twitchOAuthFlow.validateToken(casterToken, webClient);

        if (!tokenValid) {
            twitchOAuthFlow.installRefreshToken(casterToken);
            authTokenDao.save(casterToken);
            headers.put(HttpHeader.AUTHORIZATION, "OAuth " + casterToken.getToken());
        }
    }

    private Map<String, Object> listQuery(int limit, int offset) {
        Map<String, Object> query = new HashMap<>();
        query.put("limit", MathUtils.minMax(limit, 1, MAX_PAGE_SIZE));
        query.put("offset", offset);
        query.put("direction", "desc");
        return query;
    }
}
