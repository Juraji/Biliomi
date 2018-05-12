package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1;

import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth.StreamLabsOAuthFlow;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model.StreamLabsSocketToken;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.v1.model.StreamLabsTwitchUser;
import nl.juraji.biliomi.config.spotify.StreamLabsConfigService;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class StreamLabsApiImpl implements StreamLabsApi {
    private static final String API_BASE_URI = "https://streamlabs.com/api/v1.0";
    private final HttpFields headers = new HttpFields();
    @Inject
    private StreamLabsConfigService configService;
    @Inject
    private WebClient webClient;
    @Inject
    private AuthTokenDao authTokenDao;

    @PostConstruct
    private void initStreamLabsApi() {
        headers.put(HttpHeader.ACCEPT, "application/json");
        headers.put(WebClient.NO_CACHE_HEADER, "true");
    }

    @Override
    public Response<StreamLabsTwitchUser> getMe() throws Exception {
        Url url = Url.url(API_BASE_URI, "user")
                .withQueryParam("access_token", executeTokenPreflight());
        return webClient.get(url, headers, StreamLabsTwitchUser.class);
    }

    @Override
    public Response<StreamLabsSocketToken> getSocketToken() throws Exception {
        Url url = Url.url(API_BASE_URI, "socket", "token")
                .withQueryParam("access_token", executeTokenPreflight());
        return webClient.get(url, headers, StreamLabsSocketToken.class);
    }

    /**
     * Update the persisted access token and the authorization header if necessary
     *
     * @return The current access token
     */
    @SuppressWarnings("Duplicates")
    private synchronized String executeTokenPreflight() throws Exception {
        AuthToken token = authTokenDao.get(TokenGroup.INTEGRATIONS, "streamlabs");

        if (StringUtils.isEmpty(token.getToken())) {
            throw new UnavailableException("The Stream Labs is not connected to an account");
        }

        final StreamLabsOAuthFlow flow = new StreamLabsOAuthFlow(configService.getConsumerKey(), configService.getConsumerSecret());
        final boolean tokenValid = flow.validateToken(token, webClient);

        if (!tokenValid) {
            flow.installRefreshToken(token);
            authTokenDao.save(token);
        }

        return token.getToken();
    }
}
