package nl.juraji.biliomi.model.internal.yaml.usersettings;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.*;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USBiliomi {
  private USCore core;
  private USDatabase database;
  private USTwitch twitch;
  private USIntegrations integrations;
  private USComponents components;

  public USCore getCore() {
    return core;
  }

  public void setCore(USCore core) {
    this.core = core;
  }

  public USDatabase getDatabase() {
    return database;
  }

  public void setDatabase(USDatabase database) {
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

  public USComponents getComponents() {
    return components;
  }

  public void setComponents(USComponents components) {
    this.components = components;
  }
}
