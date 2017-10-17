package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class BadWordsService {

  @Inject
  private UserSettings userSettings;
  private List<String> wordList;

  @PostConstruct
  private void initBadWordsService() {
    //noinspection unchecked
    List<String> words = userSettings.getBiliomi().getComponents().getBadwords();

    if (words == null) {
      throw new SettingsDefinitionException("Missing badwords definition, check your settigns");
    }

    wordList = words;
  }

  public boolean isBadWord(String word) {
    return wordList.stream()
        .anyMatch(word::equalsIgnoreCase);
  }

  public boolean containsBadWords(String input) {
    return Arrays.stream(input.split(" "))
        .anyMatch(wordList::contains);
  }
}
