package nl.juraji.biliomi.config.spotify;

import nl.juraji.biliomi.config.ConfigService;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class SpotifyConfigService extends ConfigService<YamlSpotifyConfig> {

  public SpotifyConfigService() {
    super("integrations/spotify.yml", YamlSpotifyConfig.class);
  }

  public String getConsumerKey() {
    return config.getConsumerKey();
  }

  public String getConsumerSecret() {
    return config.getConsumerSecret();
  }
}
