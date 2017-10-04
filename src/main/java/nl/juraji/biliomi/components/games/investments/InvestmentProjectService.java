package nl.juraji.biliomi.components.games.investments;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.settings.UserSettings;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class InvestmentProjectService {

  private List<String> projects;

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initInvestmentProjectService() {
    //noinspection unchecked
    projects = (List<String>) userSettings.getObjectValue("biliomi.component.investmentgame.projects");

    if (projects == null || projects.size() == 0) {
      throw new SettingsDefinitionException("No investment projects defined, check the settings");
    }
  }

  public List<String> getList() {
    return Collections.unmodifiableList(projects);
  }

  public String getRandom() {
    return MathUtils.listRand(projects);
  }
}
