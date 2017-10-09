package nl.juraji.biliomi.model.internal.yaml.usersettings;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.UDDatabase;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USCore;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USIntegrations;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.USTwitch;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USBiliomi {
  private USCore core;
  private UDDatabase database;
  private USTwitch twitch;
  private USIntegrations integrations;

  public USCore getCore() {
    return core;
  }

  public void setCore(USCore core) {
    this.core = core;
  }

  public UDDatabase getDatabase() {
    return database;
  }

  public void setDatabase(UDDatabase database) {
    this.database = database;
  }

  public USTwitch getTwitch() {
    return twitch;
  }

  public void setTwitch(USTwitch twitch) {
    this.twitch = twitch;
  }

  public USIntegrations getIntegrations() {
    return integrations;
  }

  public void setIntegrations(USIntegrations integrations) {
    this.integrations = integrations;
  }
}
