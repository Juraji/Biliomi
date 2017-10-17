package nl.juraji.biliomi.io.api.twitch.irc.utils;

import com.google.common.base.Splitter;
import nl.juraji.biliomi.utility.calculate.EnumUtils;

import java.util.HashMap;

/**
 * Created by Juraji on 18-5-2017.
 * Biliomi v3
 */
@SuppressWarnings("unused")
public class Tags extends HashMap<String, String> {
  private static final String TAG_BADGES = "badges";
  private static final String TAG_BAN_DURATION = "ban-duration";
  private static final String TAG_BAN_REASON = "ban-reason";
  private static final String TAG_BITS = "bits";
  private static final String TAG_BROADCASTER_LANG = "broadcaster-lang";
  private static final String TAG_COLOR = "color";
  private static final String TAG_DISPLAY_NAME = "display-name";
  private static final String TAG_EMOTE_SETS = "emote-sets";
  private static final String TAG_EMOTES = "emotes";
  private static final String TAG_ID = "id";
  private static final String TAG_MOD = "mod";
  private static final String TAG_MSG_ID = "msg-id";
  private static final String TAG_MSG_PARAM_MONTHS = "msg-param-months";
  private static final String TAG_MSG_PARAM_SUB_PLAN = "msg-param-sub-plan";
  private static final String TAG_MSG_PARAM_SUB_PLAN_NAME = "msg-param-sub-plan-name";
  private static final String TAG_R9K = "r9k";
  private static final String TAG_ROOM_ID = "room-id";
  private static final String TAG_SLOW = "slow";
  private static final String TAG_SUBS_ONLY = "subs-only";
  private static final String TAG_SUBSCRIBER = "subscriber";
  private static final String TAG_SYSTEM_MSG = "system-msg";
  private static final String TAG_TURBO = "turbo";
  private static final String TAG_USER = "user";
  private static final String TAG_USER_ID = "user-id";
  private static final String TAG_USER_TYPE = "user-type";

  /**
   * Create a new Tags instance using the tags part of an IRC message
   * @param tagsString The tags string (Starting with "@")
   */
  public Tags(String tagsString) {
    if (tagsString != null) {
      putAll(Splitter
          .on(';')
          .withKeyValueSeparator('=')
          .split(tagsString.substring(1)));
    }
  }

  // Commands: CLEARCHAT
  // The moderator’s reason for the timeout or ban.
  public String getBanReason() {
    return get(TAG_BAN_REASON);
  }

  // Commands: CLEARCHAT
  // (Optional) Duration of the timeout, in seconds. If omitted, the ban is permanent.
  public String getBanDuration() {
    return get(TAG_BAN_DURATION);
  }

  // Commands: GLOBALUSERSTATE, PRIVMSG, USERNOTICE, USERSTATE
  // Hexadecimal RGB color code. This is empty if it is never set.
  public String getColor() {
    return get(TAG_COLOR);
  }

  // Commands: GLOBALUSERSTATE, PRIVMSG, USERNOTICE, USERSTATE
  // The user’s display name, escaped as described in the IRCv3 spec. This is empty if it is never set.
  public String getDisplayName() {
    return get(TAG_DISPLAY_NAME);
  }

  // Commands: GLOBALUSERSTATE
  // A comma-separated list of emotes, belonging to one or more emote sets. This always contains at least 0.
  // Get Chat Emoticons by Set gets a subset of emoticons.
  public String getEmoteSets() {
    return get(TAG_EMOTE_SETS);
  }

  // Commands: GLOBALUSERSTATE, PRIVMSG, USERNOTICE, USERSTATE
  // 1 if the user has a Turbo badge; otherwise, 0.
  public boolean getTurbo() {
    return "1".equals(get(TAG_TURBO));
  }

  // Commands: USERNOTICE
  // The name of the user who sent the notice.
  public String getUser() {
    return get(TAG_USER);
  }

  // Commands: GLOBALUSERSTATE, PRIVMSG, USERNOTICE
  // The user’s ID.
  public String getUserId() {
    return get(TAG_USER_ID);
  }

  // Commands: GLOBALUSERSTATE, PRIVMSG, USERNOTICE, USERSTATE
  // The user’s type. The broadcaster can have any of these.
  public UserType getUserType() {
    return EnumUtils.toEnum(get(TAG_USER_TYPE), UserType.class);
  }

  // Commands: PRIVMSG, USERNOTICE
  // Comma-separated list of chat badges and the version of each badge (each in the format <badge>/<version>, such as admin/1).
  // Valid badge values: admin, bits, broadcaster, global_mod, moderator, subscriber, staff, turbo.
  public String getBadges() {
    return get(TAG_BADGES);
  }

  // Commands: PRIVMSG
  // (Optional) The amount of cheer/bits employed by the user. All instances of these regular expressions:
  // /(^\|\s)<emote-name>\d+(\s\|$)/
  // (where <emote-name> is an emote name returned by the Get Cheermotes endpoint), should be replaced with the appropriate emote:
  // static-cdn.jtvnw.net/bits/<theme>/<type>/<color>/<size>
  // - theme – light or dark
  // - type – animated or static
  // - color – red for 10000+ bits, blue for 5000-9999, green for 1000-4999, purple for 100-999, gray for 1-99
  // - size – A digit between 1 and 4
  public String getBits() {
    return get(TAG_BITS);
  }

  // Commands: PRIVMSG, USERNOTICE, USERSTATE
  // Information to replace text in the message with emote images. This can be empty. Syntax:
  // <emote ID>:<first index>-<last index>,<another first index>-<another last index>/<another emote ID>:<first index>-<last index>...
  // - emote ID – The number to use in this URL:
  // http://static-cdn.jtvnw.net/emoticons/v1/:<emote ID>/:<size>
  // (size is 1.0, 2.0 or 3.0.)
  // -first index, last index – Character indexes. \001ACTION does not count. Indexing starts from the first character
  // that is part of the user’s actual message. See the example (normal message) below.
  public String getEmotes() {
    return get(TAG_EMOTES);
  }

  // Commands: PRIVMSG
  // A unique ID for the message.
  public String getId() {
    return get(TAG_ID);
  }

  // Commands: PRIVMSG, USERNOTICE, USERSTATE
  // 1 if the user has a moderator badge; otherwise, 0.
  public boolean getMod() {
    return "1".equals(get(TAG_MOD));
  }

  // Commands: USERNOTICE, NOTICE
  // The type of notice (not the ID).
  public MsgId getMsgId() {
    return EnumUtils.toEnum(get(TAG_MSG_ID), MsgId.class);
  }

  // Commands: USERNOTICE
  // The number of consecutive months the user has subscribed for, in a resub notice.
  public String getMsgParamMonths() {
    return get(TAG_MSG_PARAM_MONTHS);
  }

  // Commands: USERNOTICE
  // The type of subscription plan being used. Valid values: Prime, 1000, 2000, 3000. The number refers to
  // the first, second, and third levels of paid subscriptions, respectively (currently $4.99, $9.99, and $24.99).
  public String getMsgParamSubPlan() {
    return get(TAG_MSG_PARAM_SUB_PLAN);
  }

  // Commands: USERNOTICE
  // The display name of the subscription plan. This may be a default name or one created by the channel owner.
  public String getMsgParamSubPlanName() {
    return get(TAG_MSG_PARAM_SUB_PLAN_NAME);
  }

  // Commands: PRIVMSG, USERNOTICE
  // The channel ID.
  public String getRoomId() {
    return get(TAG_ROOM_ID);
  }

  // Commands: PRIVMSG, USERNOTICE, USERSTATE
  // 1 if the user has a subscriber badge; otherwise, 0.
  public boolean getSubscriber() {
    return "1".equals(get(TAG_SUBSCRIBER));
  }

  // Commands: USERNOTICE
  // The message printed in chat along with this notice.
  public String getSystemMsg() {
    return get(TAG_SYSTEM_MSG);
  }

  // Commands: ROOMSTATE
  // The chat language when broadcaster language mode is enabled; otherwise, empty.
  // Examples: en (English), fi (Finnish), es-MX (Mexican variant of Spanish).
  public String getBroadcasterLang() {
    return get(TAG_BROADCASTER_LANG);
  }

  // Commands: ROOMSTATE
  // R9K mode. If enabled, messages with more than 9 characters must be unique. Valid values: 0 (disabled) or 1 (enabled).
  public String getR9k() {
    return get(TAG_R9K);
  }

  // Commands: ROOMSTATE
  // The number of seconds chatters without moderator privileges must wait between sending messages.
  public String getSlow() {
    return get(TAG_SLOW);
  }

  // Commands: ROOMSTATE
  // Subscribers-only mode. If enabled, only subscribers and moderators can chat. Valid values: 0 (disabled) or 1 (enabled).
  public String getSubsOnly() {
    return get(TAG_SUBS_ONLY);
  }

  public enum MsgId {
    // USERNOTICE ids
    SUB, RESUB, CHARITY,
    // NOTICE ids
    ALREADY_BANNED, ALREADY_EMOTE_ONLY_OFF, ALREADY_EMOTE_ONLY_ON, ALREADY_R9K_OFF, ALREADY_R9K_ON, ALREADY_SUBS_OFF,
    ALREADY_SUBS_ON, BAD_HOST_HOSTING, BAD_UNBAN_NO_BAN, BAN_SUCCESS, EMOTE_ONLY_OFF, EMOTE_ONLY_ON, HOST_OFF, HOST_ON,
    HOSTS_REMAINING, MSG_CHANNEL_SUSPENDED, R9K_OFF, R9K_ON, SLOW_OFF, SLOW_ON, SUBS_OFF, SUBS_ON, TIMEOUT_SUCCESS,
    UNBAN_SUCCESS, UNRECOGNIZED_CMD, ROOM_MODS
  }

  public enum UserType {
    EMPTY, MOD, GLOBAL_MOD, ADMIN, STAFF
  }
}