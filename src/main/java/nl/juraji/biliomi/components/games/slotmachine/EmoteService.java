package nl.juraji.biliomi.components.games.slotmachine;

import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.settings.AppSettingProvider;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.estreams.EStream;
import nl.juraji.biliomi.utility.exceptions.SettingsDefinitionException;
import nl.juraji.biliomi.utility.types.Counter;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    List<Map<String, Object>> emoteData = (List<Map<String, Object>>) userSettings.getObjectValue("biliomi.component.slotMachine.emotes");
    Counter counter = new Counter();

    if (emoteData == null || emoteData.size() == 0) {
      throw new SettingsDefinitionException("No slot machine emotes defined, check the settings");
    }

    boolean incorrectEmoteData = emoteData.stream().anyMatch(map ->
        AppSettingProvider.isInvalidMapProperty("emote", String.class, map)
            || AppSettingProvider.isInvalidMapProperty("value", Integer.class, map));
    if (incorrectEmoteData) {
      throw new SettingsDefinitionException("Slot machiene emotes defined incorrectly, check the settings");
    }

    // Map the emote data to emotes and add them to the emote list
    EStream.from(emoteData)
        .mapToBiEStream(data -> (String) data.get("emote"), data -> (int) data.get("value"))
        .map((emote, value) -> new Emote(counter.increment(), emote, value))
        .forEach(emotes::add);

    // The last emote id is regarded as the jackpot emote
    // Copy it and divide the value by 3 for the bonus
    Emote lastEmote = emotes.get(emotes.size() - 1);
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
