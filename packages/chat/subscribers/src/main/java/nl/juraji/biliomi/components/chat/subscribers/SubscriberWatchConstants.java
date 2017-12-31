package nl.juraji.biliomi.components.chat.subscribers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 24-9-2017.
 * Biliomi
 */
public class SubscriberWatchConstants {
  // Templates
  public static final String SUB_NOTICE_TEMPLATE = "SubscriberSubNotice";
  public static final String RESUB_NOTICE_TEMPLATE = "SubscriberResubNotice";

  // Timer service
  public static final long FULL_UPDATE_INIT_WAIT = 10;
  public static final TimeUnit FULL_UPDATE_INIT_WAIT_TU = TimeUnit.SECONDS;
  public static final long FULL_UPDATE_INTERVAL = 6;
  public static final TimeUnit FULL_UPDATE_INTERVAL_TU = TimeUnit.HOURS;
  public static final long INCR_UPDATE_INTERVAL = 30;
  public static final TimeUnit INCR_UPDATE_INTERVAL_TU = TimeUnit.SECONDS;
}
