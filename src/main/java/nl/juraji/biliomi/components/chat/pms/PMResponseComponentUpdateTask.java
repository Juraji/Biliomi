package nl.juraji.biliomi.components.chat.pms;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import static nl.juraji.biliomi.components.chat.pms.PMResponseComponent.PM_RESPONSE_TEMPLATE_ID;

/**
 * Created by Juraji on 12-9-2017.
 * Biliomi v3
 */
@Default
public class PMResponseComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(PM_RESPONSE_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(PM_RESPONSE_TEMPLATE_ID);
      template.setTemplate("Hi my name is Biliomi, I'm a chatbot. If you have any questions please direct them towards the caster or one of the moderators");
      template.setDescription("Sent back to anyone whispering to Biliomi");
      template.getKeyDescriptions().put("moderators", "The current moderators of this channel");
      templateDao.save(template);
    }
  }

  @Override
  public void update() {
    this.install();
  }
}
