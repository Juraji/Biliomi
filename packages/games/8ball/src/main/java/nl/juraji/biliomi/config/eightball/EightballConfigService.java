package nl.juraji.biliomi.config.eightball;

import nl.juraji.biliomi.config.ConfigService;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class EightballConfigService extends ConfigService<YamlEightBallConfig> {
    public EightballConfigService() {
        super("games/8ball.yml", YamlEightBallConfig.class);
    }

    public List<String> getEightballMessages() {
        return config.getEightballMessages();
    }
}
