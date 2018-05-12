package nl.juraji.biliomi.config.adventures;

import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class YamlAdventureConfig {

    private long nextChapterInterval;
    private List<YamlAdventureStory> stories;

    public long getNextChapterInterval() {
        return nextChapterInterval;
    }

    public void setNextChapterInterval(long nextChapterInterval) {
        this.nextChapterInterval = nextChapterInterval;
    }

    public List<YamlAdventureStory> getStories() {
        return stories;
    }

    public void setStories(List<YamlAdventureStory> stories) {
        this.stories = stories;
    }
}
