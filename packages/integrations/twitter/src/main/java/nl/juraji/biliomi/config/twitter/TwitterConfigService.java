package nl.juraji.biliomi.config.twitter;

import nl.juraji.biliomi.config.ConfigService;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class TwitterConfigService extends ConfigService<YamlTwitterConfig> {

    public TwitterConfigService() {
        super("integrations/twitter.yml", YamlTwitterConfig.class);
    }

    public String getConsumerKey() {
        return config.getConsumerKey();
    }

    public String getConsumerSecret() {
        return config.getConsumerSecret();
    }
}
