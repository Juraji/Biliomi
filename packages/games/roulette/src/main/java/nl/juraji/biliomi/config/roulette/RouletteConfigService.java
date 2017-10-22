package nl.juraji.biliomi.config.roulette;

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
public class RouletteConfigService extends ConfigService<YamlRouletteConfig> {

  public RouletteConfigService() {
    super("games/roulette.yml", YamlRouletteConfig.class);
  }

  public String getRandomWin() {
    return MathUtils.listRand(config.getWins());
  }

  public String getRandomLost() {
    return MathUtils.listRand(config.getLosts());
  }
}
