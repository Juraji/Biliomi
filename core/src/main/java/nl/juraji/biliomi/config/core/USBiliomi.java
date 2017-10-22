package nl.juraji.biliomi.config.core;

import nl.juraji.biliomi.config.core.biliomi.USCore;
import nl.juraji.biliomi.config.core.biliomi.USDatabase;
import nl.juraji.biliomi.config.core.biliomi.USTwitch;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USBiliomi {
  private USCore core;
  private USDatabase database;
  private USTwitch twitch;

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
}
