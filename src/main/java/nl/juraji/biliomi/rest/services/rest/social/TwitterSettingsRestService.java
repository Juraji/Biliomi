package nl.juraji.biliomi.rest.services.rest.social;

import nl.juraji.biliomi.model.social.twitter.TwitterSettings;
import nl.juraji.biliomi.components.social.twitter.TweetTrackerService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
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
  private SettingsService settingsService;

  @Inject
  private TweetTrackerService tweetTrackerService;

  @Override
  public TwitterSettings getEntity() {
    return settingsService.getSettings(TwitterSettings.class);
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
