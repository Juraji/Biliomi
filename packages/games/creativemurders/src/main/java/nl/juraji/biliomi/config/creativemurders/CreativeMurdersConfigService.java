package nl.juraji.biliomi.config.creativemurders;

import nl.juraji.biliomi.config.ConfigService;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Templater;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class CreativeMurdersConfigService extends ConfigService<YamlCreativeMurdersConfig> {

  public CreativeMurdersConfigService() {
    super("games/creativemurders.yml", YamlCreativeMurdersConfig.class);
  }

  /**
   * Retrieve a murder message
   *
   * @param killer The killer username
   * @param target The target username
   * @return A templated message
   */
  public String getMurderMessage(String killer, String target) {
    return buildMessage(config.getMurders(), killer, target);
  }

  /**
   * Retrieve a suicide message
   *
   * @param killer The killer username
   * @return A templated message
   */
  public String getSuicideMessage(String killer) {
    return buildMessage(config.getSuicides(), killer, null);
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
