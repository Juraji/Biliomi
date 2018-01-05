package nl.juraji.biliomi.io.web.oauthflow.grants.code;

import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.net.URL;

/**
 * Created by Juraji on 30-9-2017.
 * Biliomi
 */
@Default
public class CallbackResources {

  @Inject
  @AppData("oauth.uris.callbackurl.host")
  private String callbackUrlHost;

  @Inject
  @AppData("oauth.uris.callbackurl.port")
  private int callbackUrlPort;

  @Inject
  @AppData("oauth.uris.callbackurl.path")
  private String callbackUrlPath;

  @Inject
  @AppData("oauth.pages.hashredirect")
  private String hashRedirectPageResourceName;

  @Inject
  @AppData("oauth.pages.authsuccess")
  private String authSuccessPageResourceName;

  @Inject
  @AppData("oauth.pages.authfailed")
  private String authFailedPageResourceName;

  public static CallbackResources init() {
    return CDI.current().select(CallbackResources.class).get();
  }

  public String getRedirectUri() {
    return "http://" + callbackUrlHost + ":" + callbackUrlPort + callbackUrlPath;
  }

  public String getCallbackHost() {
    return callbackUrlHost;
  }

  public int getCallbackPort() {
    return callbackUrlPort;
  }

  public URL getAuthHashRedirectPageFile() {
    return CallbackResources.class.getResource(authSuccessPageResourceName);
  }

  public URL getAuthSuccessPageFile() {
    return CallbackResources.class.getResource(authFailedPageResourceName);
  }

  public URL getAuthFailedPageFile() {
    return CallbackResources.class.getResource(authFailedPageResourceName);
  }
}
