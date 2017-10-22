package nl.juraji.biliomi.config.spotify;

import nl.juraji.biliomi.config.ConfigService;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
public class StreamLabsConfigService extends ConfigService<YamlStreamLabsConfig> {

  public StreamLabsConfigService() {
    super("integrations/streamlabs.yml", YamlStreamLabsConfig.class);
  }

  public String getConsumerKey() {
    return config.getConsumerKey();
  }

  public String getConsumerSecret() {
    return config.getConsumerSecret();
  }
}
