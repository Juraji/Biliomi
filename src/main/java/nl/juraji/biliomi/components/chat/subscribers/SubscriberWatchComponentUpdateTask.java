package nl.juraji.biliomi.components.chat.subscribers;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import static nl.juraji.biliomi.components.chat.subscribers.SubscriberWatchConstants.SUB_NOTICE_TEMPLATE;
import static nl.juraji.biliomi.components.chat.subscribers.SubscriberWatchConstants.RESUB_NOTICE_TEMPLATE;

/**
 * Created by Juraji on 12-9-2017.
 * Biliomi v3
 */
@Default
public class SubscriberWatchComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(SUB_NOTICE_TEMPLATE)) {
      Template template = new Template();
      template.setTemplateKey(SUB_NOTICE_TEMPLATE);
      template.setTemplate("Wow @{{username}}, thank you for subscribing! Here's {{points}} for you!");
      template.setDescription("Posted in the chat when a new subscriber registers");
      template.getKeyDescriptions().put("username", "The username of the new subscriber");
      template.getKeyDescriptions().put("points", "The amount of points being paid out to the subscribing user");
      templateDao.save(template);
    }

    if (!templateDao.templateExists(RESUB_NOTICE_TEMPLATE)) {
      Template template = new Template();
      template.setTemplateKey(RESUB_NOTICE_TEMPLATE);
      template.setTemplate("Wow @{{username}}, thank you for resubscribing! Here's {{points}} for you!");
      template.setDescription("Posted in the chat when a subscriber resubscribes");
      template.getKeyDescriptions().put("username", "The username of the subscriber");
      template.getKeyDescriptions().put("points", "The amount of points being paid out to the subscribing user");
      templateDao.save(template);
    }
  }

  @Override
  public void update() {
    this.install();
  }
}
