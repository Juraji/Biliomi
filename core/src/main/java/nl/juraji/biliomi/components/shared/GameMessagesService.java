package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.config.ConfigService;
import nl.juraji.biliomi.config.core.YamlGameMessagesConfig;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Templater;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class GameMessagesService extends ConfigService<YamlGameMessagesConfig> {

  public GameMessagesService() {
    super("core/gamemessages.yml", YamlGameMessagesConfig.class);
  }

  /**
   * Retrieve a win message from the default pool
   *
   * @param username The target username (Some message implement the "{{username}}" template key)
   * @return A templated message
   */
  public String getWinMessage(String username) {
    return buildMessage(config.getWins(), username);
  }

  /**
   * Retrieve a lost message from the default pool
   *
   * @param username The target username (Some message implement the "{{username}}" template key)
   * @return A templated message
   */
  public String getLostMessage(String username) {
    return buildMessage(config.getLosts(), username);
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
