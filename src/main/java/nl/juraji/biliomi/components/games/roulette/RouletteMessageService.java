package nl.juraji.biliomi.components.games.roulette;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Templater;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
public class RouletteMessageService {

  private List<String> winList;
  private List<String> lostList;

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initRouletteMessageService() {
    //noinspection unchecked
    List<String> wins = (List<String>) userSettings.getObjectValue("biliomi.component.roulette.messages.win");
    //noinspection unchecked
    List<String> lost = (List<String>) userSettings.getObjectValue("biliomi.component.roulette.messages.lost");

    if (wins == null || wins.size() == 0) {
      throw new SettingsDefinitionException("No roulette win messages defined, check the settings");
    }

    if (lost == null || lost.size() == 0) {
      throw new SettingsDefinitionException("No roulette lost messages defined, check the settings");
    }

    winList = wins;
    lostList = lost;
  }

  /**
   * Retrieve a win message
   *
   * @param username The target username
   * @return A templated message
   */
  public String getWinMessage(String username) {
    return buildMessage(winList, username);
  }

  /**
   * Retrieve a lost message
   *
   * @param username The target username
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
