package nl.juraji.biliomi.components.games.slotmachine;

import nl.juraji.biliomi.model.internal.yaml.usersettings.UserSettings;
import nl.juraji.biliomi.model.internal.yaml.usersettings.biliomi.components.slotmachinegame.USSlotMachineGameEmote;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Counter;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juraji on 29-5-2017.
 * Biliomi v3
 */
@Default
public class EmoteService {
  private final List<Emote> emotes = new ArrayList<>(5);
  private Emote jackPotEmote;

  @Inject
  private UserSettings userSettings;

  @PostConstruct
  private void initEmoteService() {
    //noinspection unchecked
    List<USSlotMachineGameEmote> emotes = userSettings.getBiliomi().getComponents().getSlotMachineGame().getEmotes();
    Counter counter = new Counter();

    if (emotes == null || emotes.size() == 0) {
      throw new SettingsDefinitionException("No slot machine emotes defined, check the settings");
    }

    // Map the emote data to emotes and add them to the emote list
    EStream.from(emotes)
        .mapToBiEStream(USSlotMachineGameEmote::getEmote, USSlotMachineGameEmote::getValue)
        .map((emote, value) -> new Emote(counter.increment(), emote, value))
        .forEach(this.emotes::add);

    // The last emote id is regarded as the jackpot emote
    // Copy it and divide the value by 3 for the bonus
    Emote lastEmote = this.emotes.get(this.emotes.size() - 1);
    jackPotEmote = new Emote(lastEmote.getId(), lastEmote.getEmote(), lastEmote.getValue() / 3);
  }

  public Emote getRandom() {
    return MathUtils.listRand(emotes);
  }

  public boolean isJackpotSeen(Emote... emotes) {
    return Arrays.stream(emotes).anyMatch(emote -> emote.getId() == jackPotEmote.getId());
  }

  public Emote getJackPotEmote() {
    return jackPotEmote;
  }
}
