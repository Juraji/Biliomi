package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
public enum UpdateModeType {
  INSTALL("create"),      // Will install the Biliomi as a fresh installation
  UPDATE("update"),       // Will run database and system update tasks
  OFF(null),          // Does nothing, just start Biliomi
  DEVELOPMENT("create-drop");   // Will drop any existing tables in the database an reinstall Biliomi

  private String ddlMode;

  UpdateModeType(String ddlMode) {
    this.ddlMode = ddlMode;
  }

  public String getDdlMode() {
    return this.ddlMode;
  }
}
