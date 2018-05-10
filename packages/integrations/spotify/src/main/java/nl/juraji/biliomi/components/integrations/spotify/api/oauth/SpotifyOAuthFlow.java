package nl.juraji.biliomi.components.integrations.spotify.api.oauth;

import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauth.OAuthFlow;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Juraji on 10-5-2018.
 * Biliomi
 */
public class SpotifyOAuthFlow extends OAuthFlow {

    public SpotifyOAuthFlow(String clientId, String clientSecret) {
        super(clientId, clientSecret,
                "https://accounts.spotify.com/authorize",
                "https://accounts.spotify.com/api/token");
    }

    @Override
    public boolean validateToken(AuthToken token, WebClient client) {
        final DateTime expiryTime = token.getExpiryTime();
        DateTime now = DateTime.now();
        return expiryTime == null || now.isAfter(expiryTime);
    }

    @Override
    protected String getAuthorizationCode(String scopes) throws OAuthSystemException, IOException, ExecutionException, InterruptedException {
        super.addImplicitGrantParameter("show_dialog", "true");
        return super.getAuthorizationCode(scopes);
    }
}
