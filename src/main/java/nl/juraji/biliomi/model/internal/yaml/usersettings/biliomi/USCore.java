package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USCore {
  private String updateMode;
  private boolean checkForUpdates;
  private String countryCode;

  public String getUpdateMode() {
    return updateMode;
  }

  public void setUpdateMode(String updateMode) {
    this.updateMode = updateMode;
  }

  public boolean isCheckForUpdates() {
    return checkForUpdates;
  }

  public void setCheckForUpdates(boolean checkForUpdates) {
    this.checkForUpdates = checkForUpdates;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }
}
