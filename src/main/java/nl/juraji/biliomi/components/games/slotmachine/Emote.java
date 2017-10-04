package nl.juraji.biliomi.components.games.slotmachine;

/**
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
public class Emote {
  private final int id;
  private final String emote;
  private final long value;

  Emote(int id, String emote, long value) {
    this.id = id;
    this.emote = emote;
    this.value = value;
  }

  public int getId() {
    return id;
  }

  public String getEmote() {
    return emote;
  }

  public long getValue() {
    return value;
  }
}
