package nl.juraji.biliomi.components.games.investments;

import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;

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
    projects = userSettings.getBiliomi().getComponents().getInvestmentGame().getProjects();

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
