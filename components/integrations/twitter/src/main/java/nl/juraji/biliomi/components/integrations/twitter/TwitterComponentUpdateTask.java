package nl.juraji.biliomi.components.integrations.twitter;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 12-9-2017.
 * Biliomi v3
 */
@Default
public class TwitterComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(TwitterComponent.TWITTER_UPDATE_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(TwitterComponent.TWITTER_UPDATE_TEMPLATE_ID);
      template.setTemplate("{{channel}} is live now with {{game}}. Join while the coffee is hot! ^{{botname}}");
      template.setDescription("Used by !tweetstatus to post updates to Twitter");
      template.getKeyDescriptions().put("channel", "The current channel name");
      template.getKeyDescriptions().put("botname", "The current bot username");
      template.getKeyDescriptions().put("game", "The current game title");
      template.getKeyDescriptions().put("status", "The current channel status");
      templateDao.save(template);
    }

    if (!templateDao.templateExists(TwitterComponent.TWITTER_TWEET_FOUND_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(TwitterComponent.TWITTER_TWEET_FOUND_TEMPLATE_ID);
      template.setTemplate("Hey @{{username}}! Thanks for tweeting out the stream <3");
      template.setDescription("Posted in the chat whenever a tweet is found for one of the tracking words set by !twitter settrackedwords");
      template.getKeyDescriptions().put("username", "The Twitter screen name of the tweeting user");
      templateDao.save(template);
    }
  }
}
