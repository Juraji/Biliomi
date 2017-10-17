package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.twitch.USTwitchLogin;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USTwitch {
  private String clientId;
  private USTwitchLogin login;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public USTwitchLogin getLogin() {
    return login;
  }

  public void setLogin(USTwitchLogin login) {
    this.login = login;
  }
}
