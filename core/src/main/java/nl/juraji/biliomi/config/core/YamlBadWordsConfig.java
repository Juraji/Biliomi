package nl.juraji.biliomi.config.core;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlBadWordsConfig {
  private List<String> badwords;

  public List<String> getBadwords() {
    return badwords;
  }

  public void setBadwords(List<String> badwords) {
    this.badwords = badwords;
  }
}
