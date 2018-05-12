package nl.juraji.biliomi.config.investments;

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
public class InvestmentsConfigService extends ConfigService<YamlInvestmentsConfig> {

    public InvestmentsConfigService() {
        super("games/investments.yml", YamlInvestmentsConfig.class);
    }

    public String getRandomProject() {
        return MathUtils.listRand(config.getProjects());
    }
}
