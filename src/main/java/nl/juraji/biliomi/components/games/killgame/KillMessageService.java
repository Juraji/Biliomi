package nl.juraji.biliomi.components.games.killgame;

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
public class KillMessageService {

  private List<String> murdersList;
  private List<String> suicidesList;

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initRouletteMessageService() {
    //noinspection unchecked
    murdersList = (List<String>) userSettings.getObjectValue("biliomi.component.killgame.murders");
    //noinspection unchecked
    suicidesList = (List<String>) userSettings.getObjectValue("biliomi.component.killgame.suicides");

    if (murdersList == null || murdersList.size() == 0) {
      throw new SettingsDefinitionException("No kill game murder messages defined, check the settings");
    }

    if (suicidesList == null || suicidesList.size() == 0) {
      throw new SettingsDefinitionException("No kill game suicide messages defined, check the settings");
    }
  }

  /**
   * Retrieve a murder message
   *
   * @param killer The killer username
   * @param target The target username
   * @return A templated message
   */
  public String getMurderMessage(String killer, String target) {
    return buildMessage(murdersList, killer, target);
  }

  /**
   * Retrieve a suicide message
   *
   * @param killer The killer username
   * @return A templated message
   */
  public String getSuicideMessage(String killer) {
    return buildMessage(suicidesList, killer, null);
  }

  private String buildMessage(List<String> list, String killer, String target) {
    String message = MathUtils.listRand(list);

    if (message == null) {
      throw new SettingsDefinitionException("Missing kill messages definition, check the settings");
    }

    return Templater.template(message)
        .add("killer", killer)
        .add("target", target)
        .apply();
  }
}
