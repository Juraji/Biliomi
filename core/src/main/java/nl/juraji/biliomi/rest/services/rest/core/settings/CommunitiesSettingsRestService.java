package nl.juraji.biliomi.rest.services.rest.core.settings;

import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.core.settings.CommunitiesSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * Created by Juraji on 16-12-2017.
 * Biliomi
 */
@Path("/core/settings/communities")
public class CommunitiesSettingsRestService extends SettingsModelRestService<CommunitiesSettings> {

  @Inject
  private SettingsService settingsService;

  @Override
  public CommunitiesSettings getEntity() {
    return settingsService.getSettings(CommunitiesSettings.class);
  }

  @Override
  public CommunitiesSettings updateEntity(CommunitiesSettings e) {
    CommunitiesSettings settings = settingsService.getSettings(CommunitiesSettings.class);

    settings.setAutoUpdateCommunities(e.isAutoUpdateCommunities());

    settings.getDefaultCommunities().clear();
    settings.getDefaultCommunities().addAll(e.getDefaultCommunities());

    settingsService.save(settings);

    return settings;
  }
}
