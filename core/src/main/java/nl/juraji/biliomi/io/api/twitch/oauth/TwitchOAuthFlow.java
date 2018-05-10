package nl.juraji.biliomi.io.api.twitch.oauth;

import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.io.web.oauth.OAuthFlow;
import nl.juraji.biliomi.model.core.security.tokens.AuthToken;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Juraji on 10-5-2018.
 * Biliomi
 */
public class TwitchOAuthFlow extends OAuthFlow {

    public TwitchOAuthFlow(String clientId, String clientSecret) {
        super(clientId, clientSecret,
                "https://id.twitch.tv/oauth2/authorize",
                "https://id.twitch.tv/oauth2/token");
    }

    @Override
    public boolean validateToken(AuthToken token, WebClient client) throws Exception {
        final HttpFields headers = new HttpFields();
        headers.put(HttpHeader.AUTHORIZATION, "OAuth " + token.getToken());
        final Response<String> response = client.get("https://id.twitch.tv/oauth2/validate", headers, String.class);
        return response.isOK() && response.getRawData().contains(clientId);
    }

    @Override
    protected String getAuthorizationCode(String scopes) throws OAuthSystemException, IOException, ExecutionException, InterruptedException {
        super.addImplicitGrantParameter("force_verify", "true");
        return super.getAuthorizationCode(scopes);
    }
}
