package nl.juraji.biliomi.config.adventures;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlAdventureStory {
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
