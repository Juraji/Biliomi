package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import java.net.URL;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
public class CallbackResources {
  public static final String REDIRECT_URI = "http://127.0.0.1:23522/oauth/authorize";
  public static final String HOST = "127.0.0.1";
  public static final int PORT = 23522;

  public static URL getAuthFailedPageFile() {
    return CallbackResources.class.getResource("/oauth/auth-failed.html");
  }

  public static URL getAuthSuccessPageFile() {
    return CallbackResources.class.getResource("/oauth/auth-success.html");
  }
}
