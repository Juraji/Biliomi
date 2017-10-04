package nl.juraji.biliomi.components.games.adventures.services;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UserSetting;
import nl.juraji.biliomi.utility.settings.AppSettingProvider;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
public class StoryService {

  private final List<Story> stories = new ArrayList<>();

  @Inject
  private UserSettings userSettings;

  @Inject
  @UserSetting("biliomi.component.adventures.nextChapterInterval")
  private String nextChapterInterval;

  @PostConstruct
  private void initStoryService() {
    //noinspection unchecked
    List<Map<String, Object>> storyData = (List<Map<String, Object>>) userSettings.getObjectValue("biliomi.component.adventures.stories");

    if (storyData == null || storyData.size() == 0) {
      throw new SettingsDefinitionException("No adventure stories defined, check the settings");
    }

    boolean incorrectStoryData = storyData.stream().anyMatch(map ->
        AppSettingProvider.isInvalidMapProperty("title", String.class, map)
            || AppSettingProvider.isInvalidMapProperty("chapters", List.class, map));
    if (incorrectStoryData) {
      throw new SettingsDefinitionException("Adventure stories defined incorrectly, check the settings");
    }

    //noinspection unchecked
    EStream.from(storyData)
        .mapToBiEStream(data -> (String) data.get("title"), data -> (List<String>) data.get("chapters"))
        .map(Story::new)
        .forEach(stories::add);
  }

  public long getNextChapterInterval() {
    return Numbers.asNumber(nextChapterInterval).toLong();
  }

  public Story getRandomStory() {
    return MathUtils.listRand(stories);
  }
}
