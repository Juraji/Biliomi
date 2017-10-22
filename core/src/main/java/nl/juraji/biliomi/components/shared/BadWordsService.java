package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.config.ConfigService;
import nl.juraji.biliomi.config.core.YamlBadWordsConfig;

import javax.enterprise.inject.Default;
import java.util.Arrays;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class BadWordsService extends ConfigService<YamlBadWordsConfig> {

  public BadWordsService() {
    super("core/badwords.yml", YamlBadWordsConfig.class);
  }

  public boolean isBadWord(String word) {
    return config.getBadwords().stream()
        .anyMatch(word::equalsIgnoreCase);
  }

  public boolean containsBadWords(String input) {
    return Arrays.stream(input.split(" "))
        .anyMatch(config.getBadwords()::contains);
  }
}
