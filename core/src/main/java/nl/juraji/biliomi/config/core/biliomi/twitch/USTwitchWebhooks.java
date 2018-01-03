package nl.juraji.biliomi.config.core.biliomi.twitch;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
public class USTwitchWebhooks {
  private String callbackUrl;
  private String callbackPort;

  public String getCallbackUrl() {
    return callbackUrl;
  }

  public void setCallbackUrl(String callbackUrl) {
    this.callbackUrl = callbackUrl;
  }

  public String getCallbackPort() {
    return callbackPort;
  }

  public void setCallbackPort(String callbackPort) {
    this.callbackPort = callbackPort;
  }
}
