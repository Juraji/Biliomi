package nl.juraji.biliomi.io.web.oauth;

import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import nl.juraji.biliomi.utility.types.TokenGenerator;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.joda.time.DateTime;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Juraji on 10-5-2018.
 * Biliomi
 */
public abstract class OAuthFlow {
    public static final String REDIRECT_URI = "http://127.0.0.1:23522/oauth/authorize";
    protected final String clientId;
    protected final String clientSecret;
    protected final String stateToken;
    protected final String authorizationEndpoint;
    protected final String tokenEndpoint;
    private Map<String, String> implicitGrantParams;

    public OAuthFlow(String clientId, String clientSecret, String authorizationEndpoint, String tokenEndpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;

        this.stateToken = new TokenGenerator(10, true).generate();
    }

    public abstract boolean validateToken(AuthToken token, WebClient client) throws Exception;

    /**
     * Install a new access token into an AuthToken object
     *
     * @param authToken The AuthToken object to populate
     * @param scopes    A string of scopes to use while authorizing
     * @throws Exception On authorization error
     */
    public void installAccessToken(AuthToken authToken, String scopes) throws Exception {
        final String authorizationCode = this.getAuthorizationCode(scopes);
        if (authorizationCode != null) {
            final OAuthAccessTokenResponse response = this.getAccessToken(authorizationCode);
            if (response != null) {
                authToken.setToken(response.getAccessToken());
                authToken.setRefreshToken(response.getRefreshToken());
                authToken.setTimeToLive(response.getExpiresIn());
                authToken.setIssuedAt(DateTime.now());
            }
        }
    }

    public void installRefreshToken(AuthToken authToken) throws OAuthProblemException, OAuthSystemException {
        final OAuthAccessTokenResponse response = this.refreshToken(authToken.getRefreshToken());
        if (response != null) {
            authToken.setToken(response.getAccessToken());
            authToken.setRefreshToken(response.getRefreshToken());
            authToken.setIssuedAt(DateTime.now());

            if (response.getExpiresIn() != null) {
                authToken.setTimeToLive(response.getExpiresIn());
            }
        }
    }

    protected String getAuthorizationCode(String scopes) throws OAuthSystemException, IOException, ExecutionException, InterruptedException {
        final OAuthClientRequest.AuthenticationRequestBuilder builder = OAuthClientRequest
                .authorizationLocation(authorizationEndpoint)
                .setResponseType(ResponseType.CODE.toString())
                .setClientId(clientId)
                .setRedirectURI(REDIRECT_URI)
                .setScope(scopes)
                .setState(stateToken);

        if (implicitGrantParams != null && implicitGrantParams.size() > 0) {
            implicitGrantParams.forEach(builder::setParameter);
        }

        Desktop.getDesktop().browse(URI.create(builder.buildQueryMessage().getLocationUri()));
        final OAuthCodeReceiver receiver = new OAuthCodeReceiver(REDIRECT_URI, stateToken);
        return receiver.awaitAccessCode();
    }

    protected OAuthAccessTokenResponse getAccessToken(String accessCode) throws OAuthSystemException, OAuthProblemException {
        final OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(tokenEndpoint)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(REDIRECT_URI)
                .setCode(accessCode)
                .buildQueryMessage();

        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request);
    }

    protected OAuthAccessTokenResponse refreshToken(String refreshToken) throws OAuthSystemException, OAuthProblemException {
        final OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(tokenEndpoint)
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(REDIRECT_URI)
                .setRefreshToken(refreshToken)
                .buildBodyMessage();

        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request, OAuth.HttpMethod.POST);
    }

    @SuppressWarnings("SameParameterValue")
    protected void addImplicitGrantParameter(String key, String value) {
        if (this.implicitGrantParams == null) {
            this.implicitGrantParams = new HashMap<>();
        }

        this.implicitGrantParams.put(key, value);
    }
}
