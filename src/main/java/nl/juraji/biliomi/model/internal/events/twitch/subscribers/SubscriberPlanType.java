package nl.juraji.biliomi.model.internal.events.twitch.subscribers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
public enum SubscriberPlanType {
  PRIME("prime"),
  TIER1("1000"),
  TIER2("2000"),
  TIER3("3000");

  private String tier;

  SubscriberPlanType(String tier) {
    this.tier = tier;
  }

  public String getTier() {
    return tier;
  }

  @JsonValue
  public static String fromSubscriberPlanType(SubscriberPlanType type) {
    return type.getTier();
  }

  @JsonCreator
  public static SubscriberPlanType fromMessagePlanType(String messagePlayType) {
    return Arrays.stream(SubscriberPlanType.values())
        .filter(planType -> planType.tier.equalsIgnoreCase(messagePlayType))
        .findFirst()
        .orElse(null);
  }
}
