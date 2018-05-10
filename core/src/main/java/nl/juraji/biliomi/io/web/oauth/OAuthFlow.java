package nl.juraji.biliomi.io.web.oauth;

import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.utility.types.TokenGenerator;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;

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
    private final String clientId;
    private final String clientSecret;
    private final String stateToken;
    private final String authorizationEndpoint;
    private final String tokenEndpoint;
    private Map<String, String> implicitGrantParams;

    public OAuthFlow(String clientId, String clientSecret, String authorizationEndpoint, String tokenEndpoint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizationEndpoint = authorizationEndpoint;
        this.tokenEndpoint = tokenEndpoint;

        this.stateToken = new TokenGenerator(10, true).generate();
    }

    public abstract boolean validateToken(String token, WebClient client) throws Exception;

    public String getImplicitGrantCode(String scopes) throws OAuthSystemException, IOException, ExecutionException, InterruptedException {

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

    public OAuthAccessTokenResponse getAccessToken(String accessCode) throws OAuthSystemException, OAuthProblemException {
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

    public OAuthAccessTokenResponse refreshToken(String refreshToken) throws OAuthSystemException, OAuthProblemException {
        final OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(tokenEndpoint)
                .setGrantType(GrantType.REFRESH_TOKEN)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(REDIRECT_URI)
                .setRefreshToken(refreshToken)
                .buildBodyMessage();

        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request);
    }

    protected void addImplicitGrantParameter(String key, String value) {
        if (this.implicitGrantParams == null) {
            this.implicitGrantParams = new HashMap<>();
        }

        this.implicitGrantParams.put(key, value);
    }

    protected String getClientId() {
        return clientId;
    }
}
