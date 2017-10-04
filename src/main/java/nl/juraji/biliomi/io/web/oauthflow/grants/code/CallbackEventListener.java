package nl.juraji.biliomi.io.web.oauthflow.grants.code;

/**
 * Created by Juraji on 28-7-2017.
 * Biliomi v3
 */
public interface CallbackEventListener {
  void onAccessTokenReceived(String accessToken, String stateToken);
  void onAuthenticationError(String error);
}
