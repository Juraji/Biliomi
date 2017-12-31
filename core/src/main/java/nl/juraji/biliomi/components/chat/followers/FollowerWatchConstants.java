package nl.juraji.biliomi.components.chat.followers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 24-9-2017.
 * Biliomi
 */
public class FollowerWatchConstants {
  // Templates
  public static final String INCOMING_FOLLOW_NOTICE = "IncomingFollowNotice";

  // Timer service
  public static final long INCR_UPDATE_INTERVAL = 30;
  public static final long FULL_UPDATE_INIT_WAIT = 10;
  public static final long FULL_UPDATE_INTERVAL = 6;
  public static final TimeUnit INCR_UPDATE_INTERVAL_TU = TimeUnit.SECONDS;
  public static final TimeUnit FULL_UPDATE_INIT_WAIT_TU = TimeUnit.SECONDS;
  public static final TimeUnit FULL_UPDATE_INTERVAL_TU = TimeUnit.HOURS;
}
