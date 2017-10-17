package nl.juraji.biliomi.components.chat.followers;

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
public class FollowerWatchComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(FollowerWatchConstants.INCOMING_FOLLOW_NOTICE)) {
      Template template = new Template();
      template.setTemplateKey(FollowerWatchConstants.INCOMING_FOLLOW_NOTICE);
      template.setTemplate("Welcome @{{username}}, thank you for the follow! I've rewarded you {{points}} for use in botgames!");
      template.setDescription("Posted in the chat when a new follower registers");
      template.getKeyDescriptions().put("username", "The username of the new follower");
      template.getKeyDescriptions().put("points", "The amount of points being paid out to the following user");
      templateDao.save(template);
    }
  }
}
