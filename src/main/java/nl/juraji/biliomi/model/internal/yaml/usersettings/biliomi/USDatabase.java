package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USDatabase {
  private boolean useH2Database;
  private String host;
  private int port;
  private String database;
  private String username;
  private String password;
  private boolean usessl;

  public boolean isUseH2Database() {
    return useH2Database;
  }

  public void setUseH2Database(boolean useH2Database) {
    this.useH2Database = useH2Database;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isUsessl() {
    return usessl;
  }

  public void setUsessl(boolean usessl) {
    this.usessl = usessl;
  }
}
