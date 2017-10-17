package nl.juraji.biliomi.components.chat.hosts;

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
public class HostWatchComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(HostWatchComponent.INCOMING_HOST_NOTICE_TEMPLATE)) {
      Template template = new Template();
      template.setTemplateKey(HostWatchComponent.INCOMING_HOST_NOTICE_TEMPLATE);
      template.setTemplate("Hey @{{username}}, thank you for hosting! I've rewarded you {{points}} cuz you're lovely!");
      template.setDescription("Posted in the chat when another channel initiates a host to your channel");
      template.getKeyDescriptions().put("username", "The hosting channel's name");
      template.getKeyDescriptions().put("points", "The amount of points being paid out to the hosting user");
      templateDao.save(template);
    }
  }
}
