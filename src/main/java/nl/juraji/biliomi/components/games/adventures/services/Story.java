package nl.juraji.biliomi.components.games.adventures.services;

import java.util.List;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
public class Story {

  private final String title;
  private final List<String> chapters;

  public Story(String title, List<String> chapters) {
    this.title = title;
    this.chapters = chapters;
  }

  public String getTitle() {
    return title;
  }

  public List<String> getChapters() {
    return chapters;
  }
}
