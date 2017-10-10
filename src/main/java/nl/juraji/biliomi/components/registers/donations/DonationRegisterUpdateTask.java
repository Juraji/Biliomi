package nl.juraji.biliomi.components.registers.donations;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.inject.Inject;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
public class DonationRegisterUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;

  @Override
  public void install() {
    if (!templateDao.templateExists(DonationsEventService.INCOMING_DONATION_NOTICE)) {
      Template template = new Template();
      template.setTemplateKey(DonationsEventService.INCOMING_DONATION_NOTICE);
      template.setTemplate("{{username}} just donated {{formattedamount}} with message \"{{message}}\". I can not thank you enough, but at least I can give you {{points}}");
      template.setDescription("Posted in the chat when a new StreamLabs donation is registered");
      template.getKeyDescriptions().put("username", "The username of the donator");
      template.getKeyDescriptions().put("points", "The amount of points being paid out to the donator");
      template.getKeyDescriptions().put("formattedamount", "The amount the donator donated (with currency sign)");
      template.getKeyDescriptions().put("message", "The message the donator supplied with the donation");
      templateDao.save(template);
    }
  }
}
