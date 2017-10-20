package nl.juraji.biliomi.components.games.tamagotchi.services;

/**
 * Created by Juraji on 1-6-2017.
 * Biliomi v3
 */
public class TamagotchiConstants {
  public static final int AGING_INTERVAL_HOURS = 1;
  public static final double PROPERTY_TTL_HOURS = 24.0;
  public static final double PROPERTY_DECAY_PER_HOUR = (double) AGING_INTERVAL_HOURS / PROPERTY_TTL_HOURS;
  public static final double MOOD_BORED_THRESHOLD = 0.5;
  public static final double MOOD_SAD_THRESHOLD = 0.3;
}
