package nl.juraji.biliomi.components.integrations.spotify.api.v1;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.components.integrations.spotify.api.oauth.SpotifyOAuthFlow;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.SpotifySnapshot;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.player.SpotifyCurrentTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.playlist.SpotifyPlaylist;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTrack;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTrackUriList;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.tracks.SpotifyTracksSearchResult;
import nl.juraji.biliomi.components.integrations.spotify.api.v1.model.user.SpotifyUser;
import nl.juraji.biliomi.config.spotify.SpotifyConfigService;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Default
@Singleton
public class SpotifyApiImpl implements SpotifyApi {
    private static final String OAUTH_HEADER_PREFIX = "Bearer ";
    private static final String API_BASE_URI = "https://api.spotify.com/v1";
    private final HttpFields headers = new HttpFields();
    @Inject
    private SpotifyConfigService configService;
    @Inject
    @CoreSetting("biliomi.core.countryCode")
    private String countryCode;
    @Inject
    private WebClient webClient;
    @Inject
    private AuthTokenDao authTokenDao;

    @PostConstruct
    private void initSpotifyApi() {
        headers.put(HttpHeader.ACCEPT, "application/json");
        headers.put(WebClient.NO_CACHE_HEADER, "true");
    }

    @Override
    public boolean isAvailable() throws Exception {
        return executeTokenPreflight() != null;
    }

    @Override
    public Response<SpotifyUser> getMe() throws Exception {
        executeTokenPreflight();
        return webClient.get(Url.url(API_BASE_URI, "me"), headers, SpotifyUser.class);
    }

    @Override
    public Response<SpotifyCurrentTrack> getMeCurrentlyPlaying() throws Exception {
        executeTokenPreflight();
        return webClient.get(Url.url(API_BASE_URI, "me", "player", "currently-playing"), headers, SpotifyCurrentTrack.class);
    }

    @Override
    public Response<SpotifyPlaylist> getPlaylist(String playlistId) throws Exception {
        String userId = executeTokenPreflight();
        return webClient.get(Url.url(API_BASE_URI, "users", userId, "playlists", playlistId), headers, SpotifyPlaylist.class);
    }

    @Override
    public Response<SpotifySnapshot> addToTracksPlaylist(String playlistId, String... trackIds) throws Exception {
        String userId = executeTokenPreflight();

        SpotifyTrackUriList trackUriList = new SpotifyTrackUriList();
        trackUriList.getUris().addAll(Arrays.asList(trackIds));
        String trackUriListMarshal = JacksonMarshaller.marshal(trackUriList);

        Url url = Url.url(API_BASE_URI, "users", userId, "playlists", playlistId, "tracks");
        return webClient.post(url, headers, trackUriListMarshal, MediaType.JSON_UTF_8, SpotifySnapshot.class);
    }

    @Override
    public Response<SpotifyTrack> getTrack(String trackId) throws Exception {
        executeTokenPreflight();

        Url url = Url.url(API_BASE_URI, "tracks", trackId)
                .withQueryParam("market", countryCode);
        return webClient.get(url, headers, SpotifyTrack.class);
    }

    @Override
    public Response<SpotifyTracksSearchResult> searchTrack(String query, int limit) throws Exception {
        Url url = Url.url(API_BASE_URI, "search")
                .withQueryParam("query", query)
                .withQueryParam("market", countryCode)
                .withQueryParam("limit", limit)
                .withQueryParam("type", "track");
        return webClient.get(url, headers, SpotifyTracksSearchResult.class);
    }

    /**
     * Update the persisted access token and the authorization header if necessary
     *
     * @return The id of the currently linked user
     */
    @SuppressWarnings("Duplicates")
    private synchronized String executeTokenPreflight() throws Exception {
        AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "spotify");

        if (StringUtils.isEmpty(token.getToken())) {
            throw new UnavailableException("The Spotify Api is not connected to an account");
        }

        final SpotifyOAuthFlow flow = new SpotifyOAuthFlow(configService.getConsumerKey(), configService.getConsumerSecret());
        final boolean tokenValid = flow.validateToken(token, webClient);

        if (!tokenValid) {
            flow.installRefreshToken(token);
            authTokenDao.save(token);
        }

        headers.put(HttpHeader.AUTHORIZATION, OAUTH_HEADER_PREFIX + token.getToken());
        return token.getUserId();
    }
}
