package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.oauth;

import java.util.Arrays;

public enum StreamLabsOAuthScope {
  DONATIONS_CREATE("donations.create"),
  DONATIONS_READ("donations.read"),
  ALERTS_CREATE("alerts.create"),
  LEGACY_TOKEN("legacy.token"),
  SOCKET_TOKEN("socket.token"),
  POINTS_READ("points.read"),
  POINTS_WRITE("points.write");

  private String key;

  StreamLabsOAuthScope(String key) {
    this.key = key;
  }

  public static String join(StreamLabsOAuthScope... scopes) {
    if (scopes == null) return "";
    return Arrays.stream(scopes)
        .map(StreamLabsOAuthScope::getKey)
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