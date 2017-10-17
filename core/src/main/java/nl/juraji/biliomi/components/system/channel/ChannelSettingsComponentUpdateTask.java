package nl.juraji.biliomi.components.system.channel;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import static nl.juraji.biliomi.components.system.channel.ChannelSettingsComponent.CHANNEL_TITLE_TEMPLATE_KEY;

/**
 * Created by Juraji on 12-9-2017.
 * Biliomi v3
 */
@Default
public class ChannelSettingsComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(CHANNEL_TITLE_TEMPLATE_KEY)) {
      Template template = new Template();
      template.setTemplateKey(CHANNEL_TITLE_TEMPLATE_KEY);
      template.setDescription("Used to format input when the channel status is being changed.");
      template.getKeyDescriptions().put("input", "The actual input for the command");
      templateDao.save(template);
    }
  }
}
