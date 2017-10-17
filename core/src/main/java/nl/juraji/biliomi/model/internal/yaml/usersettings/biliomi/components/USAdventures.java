package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.adventures.USAdventuresStories;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USAdventures {
  private int nextChapterInterval;
  private List<USAdventuresStories> stories;

  public int getNextChapterInterval() {
    return nextChapterInterval;
  }

  public void setNextChapterInterval(int nextChapterInterval) {
    this.nextChapterInterval = nextChapterInterval;
  }

  public List<USAdventuresStories> getStories() {
    return stories;
  }

  public void setStories(List<USAdventuresStories> stories) {
    this.stories = stories;
  }
}
