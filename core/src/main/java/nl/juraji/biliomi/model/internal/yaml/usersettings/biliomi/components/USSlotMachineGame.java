package nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components;

import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.slotmachinegame.USSlotMachineGameEmote;

import java.util.List;

/**
 * Created by Juraji on 9-10-2017.
 * Biliomi
 */
public class USSlotMachineGame {
  private List<USSlotMachineGameEmote> emotes;

  public List<USSlotMachineGameEmote> getEmotes() {
    return emotes;
  }

  public void setEmotes(List<USSlotMachineGameEmote> emotes) {
    this.emotes = emotes;
  }
}
