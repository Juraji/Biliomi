package nl.juraji.biliomi.rest.services;

import nl.juraji.biliomi.components.integrations.twitter.TweetTrackerService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.model.core.security.tokens.AuthTokenDao;
import nl.juraji.biliomi.model.core.security.tokens.TokenGroup;
import nl.juraji.biliomi.model.integrations.TwitterSettings;
import nl.juraji.biliomi.rest.config.SettingsModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Set;

/**
 * Created by Juraji on 13-9-2017.
 * Biliomi
 */
@Path("/social/twitter/settings")
public class TwitterSettingsRestService extends SettingsModelRestService<TwitterSettings> {

  @Inject
  private AuthTokenDao authTokenDao;

  @Inject
  private SettingsService settingsService;

  @Inject
  private TweetTrackerService tweetTrackerService;

  @Override
  public TwitterSettings getEntity() {
    TwitterSettings settings = settingsService.getSettings(TwitterSettings.class);

    // Set integration enabled process variable
    settings.set_integrationEnabled(authTokenDao.isTokenPresent(TokenGroup.INTEGRATIONS, "twitter"));

    return settings;
  }

  @Override
  public TwitterSettings updateEntity(TwitterSettings e) {
    TwitterSettings settings = settingsService.getSettings(TwitterSettings.class);

    // Check if both TrackedKeywords lists are equal
    // If they are, nothing should be done and we can return the settings object
    Set<String> dbWords = settings.getTrackedKeywords();
    Set<String> entWords = e.getTrackedKeywords();
    if (dbWords.containsAll(entWords) && entWords.containsAll(dbWords)) {
      return settings;
    }


    dbWords.clear();
    dbWords.addAll(e.getTrackedKeywords());
    settingsService.save(settings);
    tweetTrackerService.restart();

    return settings;
  }
}
