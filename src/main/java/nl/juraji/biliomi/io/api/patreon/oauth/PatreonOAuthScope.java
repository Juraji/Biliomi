package nl.juraji.biliomi.io.api.patreon.oauth;

import java.util.Arrays;

/**
 * Created by Juraji on 11-10-2017.
 * Biliomi
 */
public enum PatreonOAuthScope {
  USERS("users"),
  PLEDGES_TO_ME("pledges-to-me"),
  MY_CAMPAIGN("my-campaign");

  private String key;

  PatreonOAuthScope(String key) {
    this.key = key;
  }

  public static String join(PatreonOAuthScope... scopes) {
    if (scopes == null) return "";
    return Arrays.stream(scopes)
        .map(PatreonOAuthScope::getKey)
        .reduce((l, r) -> l + ' ' + r)
        .orElse("");
  }

  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return key;
  }
}
