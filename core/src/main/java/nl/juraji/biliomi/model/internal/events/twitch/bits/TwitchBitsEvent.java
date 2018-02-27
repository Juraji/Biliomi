package nl.juraji.biliomi.model.internal.events.twitch.bits;

import nl.juraji.biliomi.model.internal.events.twitch.TwitchEvent;

/**
 * Created by Juraji on 27-2-2018.
 * Biliomi
 */
public class TwitchBitsEvent extends TwitchEvent {
  private String userId;
  private long bitsAmount;

  public TwitchBitsEvent(String userId, long bitsAmount) {
    this.userId = userId;
    this.bitsAmount = bitsAmount;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public long getBitsAmount() {
    return bitsAmount;
  }

  public void setBitsAmount(long bitsAmount) {
    this.bitsAmount = bitsAmount;
  }
}
