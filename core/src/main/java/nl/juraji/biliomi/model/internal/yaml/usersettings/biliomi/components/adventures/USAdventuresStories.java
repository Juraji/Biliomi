package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.adventures;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USAdventuresStories {
  private String title;
  private List<String> chapters;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getChapters() {
    return chapters;
  }

  public void setChapters(List<String> chapters) {
    this.chapters = chapters;
  }
}
