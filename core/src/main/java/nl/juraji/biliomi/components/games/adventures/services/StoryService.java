package nl.juraji.biliomi.components.games.adventures.services;

import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.adventures.USAdventuresStories;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
public class StoryService {

  private final List<Story> stories = new ArrayList<>();

  @Inject
  private UserSettings userSettings;
  private long nextChapterInterval;

  @PostConstruct
  private void initStoryService() {
    List<USAdventuresStories> stories = userSettings.getBiliomi().getComponents().getAdventures().getStories();
    nextChapterInterval = userSettings.getBiliomi().getComponents().getAdventures().getNextChapterInterval();

    if (stories == null || stories.size() == 0) {
      throw new SettingsDefinitionException("No adventure stories defined, check the settings");
    }

    EStream.from(stories)
        .mapToBiEStream(USAdventuresStories::getTitle, USAdventuresStories::getChapters)
        .map(Story::new)
        .forEach(this.stories::add);
  }

  public long getNextChapterInterval() {
    return nextChapterInterval;
  }

  public Story getRandomStory() {
    return MathUtils.listRand(stories);
  }
}
