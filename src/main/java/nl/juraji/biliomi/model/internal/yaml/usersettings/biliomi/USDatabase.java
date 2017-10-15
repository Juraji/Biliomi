package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.database.USMySQL;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USDatabase {
  private boolean useH2Database;
  private USMySQL mySQL;

  public boolean isUseH2Database() {
    return useH2Database;
  }

  public void setUseH2Database(boolean useH2Database) {
    this.useH2Database = useH2Database;
  }

  public USMySQL getMySQL() {
    return mySQL;
  }

  public void setMySQL(USMySQL mySQL) {
    this.mySQL = mySQL;
  }
}
