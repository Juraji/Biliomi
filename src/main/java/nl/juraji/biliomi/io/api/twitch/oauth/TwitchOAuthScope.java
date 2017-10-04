package nl.juraji.biliomi.io.api.twitch.oauth;

import java.util.Arrays;

public enum TwitchOAuthScope {
  USER_READ("user_read"),
  USER_BLOCKS_EDIT("user_blocks_edit"),
  USER_BLOCKS_READ("user_blocks_read"),
  USER_FOLLOWS_EDIT("user_follows_edit"),
  CHANNEL_READ("channel_read"),
  CHANNEL_EDITOR("channel_editor"),
  CHANNEL_COMMERCIAL("channel_commercial"),
  CHANNEL_STREAM("channel_stream"),
  CHANNEL_SUBSCRIPTIONS("channel_subscriptions"),
  USER_SUBSCRIPTIONS("user_subscriptions"),
  CHANNEL_CHECK_SUBSCRIPTION("channel_check_subscription"),
  CHAT_LOGIN("chat_login");

  private String key;

  TwitchOAuthScope(String key) {
    this.key = key;
  }

  public static String join(TwitchOAuthScope... scopes) {
    if (scopes == null) return "";
    return Arrays.stream(scopes)
        .map(TwitchOAuthScope::getKey)
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
