package nl.juraji.biliomi.components.chat.bits;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 10-10-2017.
 * Biliomi
 */
@Default
public class BitsComponentSetupTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(BitsComponentConstants.BITS_CHEERED_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(BitsComponentConstants.BITS_CHEERED_TEMPLATE_ID);
      template.setTemplate("{{username}} cheered {{amount}} bits! I think an applause is in order!");
      template.setDescription("Posted in the chat when bits are cheered");
      template.getKeyDescriptions().put("username", "The display name of the user cheering bits");
      template.getKeyDescriptions().put("amount", "The amount of bits being cheered");
      templateDao.save(template);
    }

    if (!templateDao.templateExists(BitsComponentConstants.BITS_PAYOUT_TO_CHEERER_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(BitsComponentConstants.BITS_PAYOUT_TO_CHEERER_TEMPLATE_ID);
      template.setTemplate("@{{username}}, {{payout}} for you!");
      template.setDescription("Posted in the chat when a payout occurs to the cheerer");
      template.getKeyDescriptions().put("username", "The display name of the user cheering bits");
      template.getKeyDescriptions().put("payout", "The amount of points being payed out");
      templateDao.save(template);
    }

    if (!templateDao.templateExists(BitsComponentConstants.BITS_PAYOUT_TO_CHATTERS_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(BitsComponentConstants.BITS_PAYOUT_TO_CHATTERS_TEMPLATE_ID);
      template.setTemplate("And for you, and you... {{payout}} for everyone!");
      template.setDescription("Posted in the chat when payout to all chatters is enabled");
      template.getKeyDescriptions().put("payout", "The amount of points being payed out");
      templateDao.save(template);
    }
  }
}
