package nl.juraji.biliomi.config.slotmachine;

import nl.juraji.biliomi.config.ConfigService;
import nl.juraji.biliomi.utility.calculate.MathUtils;
import nl.juraji.biliomi.utility.types.Counter;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
@Singleton
public class SlotmachineConfigService extends ConfigService<YamlSlotmachineConfig> {
  private final YamlSlotmachineEmote jackpot;

  public SlotmachineConfigService() {
    super("games/slotmachine.yml", YamlSlotmachineConfig.class);

    Counter counter = new Counter();
    config.getEmotes().forEach(emote -> emote.setIndex(counter.increment()));
    jackpot = config.getEmotes().get(config.getEmotes().size() - 1);
  }

  public YamlSlotmachineEmote getRandomEmote() {
    return MathUtils.listRand(config.getEmotes());
  }

  public boolean isJackpotSeen(YamlSlotmachineEmote... emotes) {
    return Arrays.stream(emotes).anyMatch(emote -> emote.getIndex() == jackpot.getIndex());
  }

  public YamlSlotmachineEmote getJackpot() {
    return jackpot;
  }
}
