package nl.juraji.biliomi.config.adventures;

import nl.juraji.biliomi.config.ConfigService;
import nl.juraji.biliomi.utility.calculate.MathUtils;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class AdventuresConfigService extends ConfigService<YamlAdventureConfig> {

    public AdventuresConfigService() {
        super("games/adventures.yml", YamlAdventureConfig.class);
    }

    public long getNextChapterInterval() {
        return config.getNextChapterInterval();
    }

    public YamlAdventureStory getRandomStory() {
        return MathUtils.listRand(config.getStories());
    }
}
