package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Templater;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class GameMessagesService {

  private List<String> winList;
  private List<String> lostList;

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initGameMessagesService() {
    //noinspection unchecked
    List<String> win = (List<String>) userSettings.getObjectValue("biliomi.component.gamemessages.win");
    //noinspection unchecked
    List<String> lost = (List<String>) userSettings.getObjectValue("biliomi.component.gamemessages.lost");

    if (win == null || win.size() == 0) {
      throw new SettingsDefinitionException("No win messages defined, check the settings");
    }

    if (lost == null || lost.size() == 0) {
      throw new SettingsDefinitionException("No lost messages defined, check the settings");
    }

    winList = win;
    lostList = lost;
  }

  /**
   * Retrieve a win message from the default pool
   *
   * @param username The target username (Some message implement the "{{username}}" template key)
   * @return A templated message
   */
  public String getWinMessage(String username) {
    return buildMessage(winList, username);
  }

  /**
   * Retrieve a lost message from the default pool
   *
   * @param username The target username (Some message implement the "{{username}}" template key)
   * @return A templated message
   */
  public String getLostMessage(String username) {
    return buildMessage(lostList, username);
  }

  private String buildMessage(List<String> list, String username) {
    String message = MathUtils.listRand(list);

    if (message == null) {
      throw new SettingsDefinitionException("Missing game messages definition, check the settings");
    }

    return Templater.template(message)
        .add("username", username)
        .apply();
  }
}
