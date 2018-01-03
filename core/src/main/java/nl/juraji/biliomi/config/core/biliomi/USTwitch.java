package nl.juraji.biliomi.config.core.biliomi;

import nl.juraji.biliomi.config.core.biliomi.twitch.USTwitchLogin;
import nl.juraji.biliomi.config.core.biliomi.twitch.USTwitchWebhooks;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTwitch {
  private String clientId;
  private USTwitchWebhooks webhooks;
  private USTwitchLogin login;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public USTwitchWebhooks getWebhooks() {
    return webhooks;
  }

  public void setWebhooks(USTwitchWebhooks webhooks) {
    this.webhooks = webhooks;
  }

  public USTwitchLogin getLogin() {
    return login;
  }

  public void setLogin(USTwitchLogin login) {
    this.login = login;
  }
}
