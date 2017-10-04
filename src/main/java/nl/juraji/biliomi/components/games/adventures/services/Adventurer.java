package nl.juraji.biliomi.components.games.adventures.services;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.games.Tamagotchi;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
public class Adventurer {
  private final User user;
  private final Tamagotchi tamagotchi;
  private final long bet;
  private final long payout;

  public Adventurer(User user, long bet, double payoutMultiplier) {
    this.user = user;
    this.tamagotchi = null;
    this.bet = bet;
    this.payout = (long) (bet * payoutMultiplier);
  }

  public Adventurer(Tamagotchi tamagotchi, long bet, double payoutMultiplier) {
    this.user = tamagotchi.getOwner();
    this.tamagotchi = tamagotchi;
    this.bet = bet;
    this.payout = (long) (bet * payoutMultiplier);
  }

  public String getName() {
    if (tamagotchi != null) {
      return tamagotchi.getName();
    } else {
      return user.getDisplayName();
    }
  }

  public User getUser() {
    return user;
  }

  public Tamagotchi getTamagotchi() {
    return tamagotchi;
  }

  public long getBet() {
    return bet;
  }

  public long getPayout() {
    return payout;
  }

  public boolean isTamagotchi() {
    return tamagotchi != null;
  }
}
