package nl.juraji.biliomi.utility.settings;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
public enum UpdateModeType {
  INSTALL,      // Will install the Biliomi as a fresh installation
  UPDATE,       // Will run database and system update tasks
  OFF,          // Does nothing, just start Biliomi
  DEVELOPMENT   // Will drop any existing tables in the database an reinstall Biliomi
}
