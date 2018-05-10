package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.utility.types.TokenGenerator;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Deprecated
public abstract class OAuthFlowDirector<E> {

  private final String consumerKey;
  private final String stateToken;
  private final CallbackResources resources;

  protected String accessToken;
  protected String refreshToken;
  protected String authenticationError;
  protected long timeToLive;

  public OAuthFlowDirector(String consumerKey) {
    if (StringUtils.isEmpty(consumerKey)) {
      throw new IllegalArgumentException("consumerKey may not be NULL or empty");
    }

    this.consumerKey = consumerKey;
    this.stateToken = new TokenGenerator(10, true).generate();
    this.resources = CallbackResources.init();
  }

  protected String getConsumerKey() {
    return consumerKey;
  }

  protected String getStateToken() {
    return stateToken;
  }

  protected String getRedirectUri() {
    return resources.getRedirectUri();
  }

  protected CallbackServer createCallbackServer() {
    return new CallbackServer(resources, stateToken);
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public String getAuthenticationError() {
    return authenticationError;
  }

  public long getTimeToLive() {
    return timeToLive;
  }

  public abstract boolean awaitAccessToken() throws IOException;

  public abstract String getAuthenticationUri(E[] scopes);
}
