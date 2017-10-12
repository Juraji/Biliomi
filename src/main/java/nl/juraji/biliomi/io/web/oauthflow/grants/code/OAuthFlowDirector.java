package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.utility.types.TokenGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
public abstract class OAuthFlowDirector<E> {

  private final String consumerKey;
  private final String stateToken;

  public OAuthFlowDirector(String consumerKey) {
    if (StringUtils.isEmpty(consumerKey)) {
      throw new IllegalArgumentException("consumerKey may not be NULL or empty");
    }

    this.consumerKey = consumerKey;
    this.stateToken = new TokenGenerator(10, true).generate();
  }

  protected String getConsumerKey() {
    return consumerKey;
  }

  protected String getStateToken() {
    return stateToken;
  }

  protected String getRedirectUri() {
    return CallbackResources.REDIRECT_URI;
  }

  public abstract String getAuthenticationUri(E[] scopes);

  public abstract boolean awaitAccessToken() throws IOException;

  public abstract String getAccessToken();

  public abstract String getAuthenticationError();
}
