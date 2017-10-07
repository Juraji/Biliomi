package nl.juraji.biliomi.components.registers.raids;

import nl.juraji.biliomi.boot.SetupTask;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import static nl.juraji.biliomi.components.registers.raids.RaidRegisterComponent.INCOMING_RAID_MESSAGE_TEMPLATE_ID;
import static nl.juraji.biliomi.components.registers.raids.RaidRegisterComponent.RAID_MESSAGE_TEMPLATE_ID;

/**
 * Created by Juraji on 12-9-2017.
 * Biliomi v3
 */
@Default
public class RaidRegisterComponentUpdateTask implements SetupTask {

  @Inject
  private TemplateDao templateDao;


  @Override
  public void install() {
    if (!templateDao.templateExists(RAID_MESSAGE_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(RAID_MESSAGE_TEMPLATE_ID);
      template.setTemplate("We are raiding {{channeldisplayname}} over at https://www.twitch.tv/{{channelname}}");
      template.setDescription("Used when throwing outgoing raids using the Raid Register");
      template.getKeyDescriptions().put("channeldisplayname", "The target channel's display name");
      template.getKeyDescriptions().put("channelname", "The target channel name");
      templateDao.save(template);
    }

    if (!templateDao.templateExists(INCOMING_RAID_MESSAGE_TEMPLATE_ID)) {
      Template template = new Template();
      template.setTemplateKey(INCOMING_RAID_MESSAGE_TEMPLATE_ID);
      template.setTemplate("We've just been raided by {{channelname}} for the {{ordinalrecordcount}} time! {{channelname}} was playing {{game}}.");
      template.setDescription("Used when registering incoming raids using the Raid Register");
      template.getKeyDescriptions().put("channelname", "The target channel's display name");
      template.getKeyDescriptions().put("ordinalrecordcount", "The nth count this channel has raided (current record count)");
      template.getKeyDescriptions().put("game", "The target channel current game");
      templateDao.save(template);
    }
  }
}
