package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth;

import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauth.OAuthFlow;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.joda.time.DateTime;

/**
 * Created by Juraji on 10-5-2018.
 * Biliomi
 */
public class StreamLabsOAuthFlow extends OAuthFlow {

    public StreamLabsOAuthFlow(String clientId, String clientSecret) {
        super(clientId, clientSecret,
                "https://streamlabs.com/api/v1.0/authorize",
                "https://streamlabs.com/api/v1.0/token");
    }

    @Override
    public boolean validateToken(AuthToken token, WebClient client) {
        final DateTime expiryTime = token.getExpiryTime();
        DateTime now = DateTime.now();
        return expiryTime == null || now.isAfter(expiryTime);
    }

    @Override
    protected OAuthAccessTokenResponse getAccessToken(String accessCode) throws OAuthSystemException, OAuthProblemException {
        final OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(tokenEndpoint)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectURI(REDIRECT_URI)
                .setCode(accessCode)
                .buildBodyMessage();

        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        return oAuthClient.accessToken(request, OAuth.HttpMethod.POST);
    }
}
