package nl.juraji.biliomi.rest.services.rest.games.settings;

import nl.juraji.biliomi.model.games.settings.InvestmentSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.ws.rs.Path;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/games/settings/investments")
public class InvestmentSettingsRestService extends SettingsModelRestService<InvestmentSettings> {

  @Override
  public InvestmentSettings getEntity() {
    return settingsService.getSettings(InvestmentSettings.class);
  }

  @Override
  public InvestmentSettings updateEntity(InvestmentSettings e) {
    InvestmentSettings settings = settingsService.getSettings(InvestmentSettings.class);

    settings.setInvestmentDuration(e.getInvestmentDuration());
    settings.setMinInterest(e.getMinInterest());
    settings.setMaxInterest(e.getMaxInterest());

    settingsService.save(settings);
    return settings;
  }
}
