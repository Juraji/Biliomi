package nl.juraji.biliomi.components.shared;

import nl.juraji.biliomi.utility.types.collections.L10nMap;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatterBuilder;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static nl.juraji.biliomi.utility.calculate.MathUtils.isPlural;

/**
 * Created by Juraji on 30-4-2017.
 * Biliomi v3
 */
@Default
public class TimeFormatter {
  private static final long MILLIS_IN_MONTH = 2629746000L;

  private static final String TIMEUNIT_SECOND = "TimeUnit.second";
  private static final String TIMEUNIT_SECONDS = "TimeUnit.seconds";
  private static final String TIMEUNIT_MINUTE = "TimeUnit.minute";
  private static final String TIMEUNIT_MINUTES = "TimeUnit.minutes";
  private static final String TIMEUNIT_HOUR = "TimeUnit.hour";
  private static final String TIMEUNIT_HOURS = "TimeUnit.hours";
  private static final String TIMEUNIT_DAY = "TimeUnit.day";
  private static final String TIMEUNIT_DAYS = "TimeUnit.days";
  private static final String TIMEUNIT_MONTH = "TimeUnit.month";
  private static final String TIMEUNIT_MONTHS = "TimeUnit.months";
  private static final String TIMEUNIT_YEAR = "TimeUnit.year";
  private static final String TIMEUNIT_YEARS = "TimeUnit.years";

  @Inject
  private L10nMap l10n;

  public DateTime now() {
    return DateTime.now();
  }

  /**
   * Format to full datetime string
   *
   * @param source The DateTime to format
   * @return A string formatted to DateTimePatterns.FullDateTime
   */
  public String fullDateTime(DateTime source) {
    return source.toString(l10n.getString("DateTimePatterns.DateTime"));
  }

  /**
   * Format to full date string
   *
   * @param source The DateTime to format
   * @return A string formatted to DateTimePatterns.FullDate
   */
  public String fullDate(DateTime source) {
    return source.toString(l10n.getString("DateTimePatterns.Date"));
  }

  /**
   * The amount of millis as a human readable quantity of time
   *
   * @param millis The amount of milliseconds in the future
   * @return A formatted string describing the remaining time
   */
  public String timeQuantity(long millis) {
    return timeQuantityUntil(now().withDurationAdded(millis, 1));
  }

  /**
   * Format to a human readable string, limited to a unit
   *
   * @param millis   The amount of milliseconds
   * @param timeUnit The TimeUnit to display
   * @return A string with the amount of time formatted as hours and minutes
   */
  public String timeQuantity(long millis, TimeUnit timeUnit) {
    // The period needs to be calculated with a Duration object, since
    // Period::millis does not support long values
    Duration duration = new Duration(millis);
    return timeQuantity(duration, timeUnit);
  }

  /**
   * Format a duration (Quantity of time) to human readable string
   *
   * @param duration   The period to format
   * @param targetUnit The target unit
   * @return A string formatted as [amount] [l10n scalar descriptor]
   */
  public String timeQuantity(Duration duration, TimeUnit targetUnit) {
    switch (targetUnit) {
      case DAYS:
        long days = duration.getStandardDays();
        return days + " " + l10n.getIfElse(isPlural(days), TIMEUNIT_DAYS, TIMEUNIT_DAY);
      case HOURS:
        long hours = duration.getStandardHours();
        return hours + " " + l10n.getIfElse(isPlural(hours), TIMEUNIT_HOURS, TIMEUNIT_HOUR);
      case MINUTES:
        long minutes = duration.getStandardMinutes();
        return minutes + " " + l10n.getIfElse(isPlural(minutes), TIMEUNIT_MINUTES, TIMEUNIT_MINUTE);
      case SECONDS:
        long seconds = duration.getStandardSeconds();
        return seconds + " " + l10n.getIfElse(isPlural(seconds), TIMEUNIT_SECONDS, TIMEUNIT_SECOND);
      default:
        throw new UnsupportedOperationException("Quantifying " + targetUnit.toString() + " is not supported");
    }
  }

  /**
   * Format a duration in milliseconds to a digital clock string
   *
   * @param millis The amount of millis
   * @return A string: [hours:]minutes:seconds
   */
  public String digitalClockQuantity(long millis) {
    Duration duration = new Duration(millis);
    Function<Long, String> pad = l -> StringUtils.leftPad(String.valueOf(l), 2, "0");

    long hours = duration.getStandardHours();
    long minutes = duration.getStandardMinutes() - (hours * 60);
    long seconds = duration.getStandardSeconds() - (minutes * 60 + hours * 3600);

    if (hours > 0) {
      return pad.apply(hours) + ':' + pad.apply(minutes) + ':' + pad.apply(seconds);
    }
    return pad.apply(minutes) + ':' + pad.apply(seconds);
  }

  /**
   * Shortcut method
   * Format the time between since and now()
   *
   * @param since A DateTime describing the start instant
   * @return A formatted string describing the ellapsed time
   */
  public String timeQuantitySince(DateTime since) {
    return timeQuantityBetween(since, now());
  }

  /**
   * Shortcut method
   * Format the time between now() and until
   *
   * @param until A DateTime describing the end instant
   * @return A formatted string describing the remaining time
   */
  public String timeQuantityUntil(DateTime until) {
    return timeQuantityBetween(now(), until);
  }

  /**
   * Format the duration between since and until to a human readable string
   *
   * @param since Start DateTime
   * @param until End DateTime
   * @return A formatted string describing the time between since and until
   */
  public String timeQuantityBetween(DateTime since, DateTime until) {
    long diff = new Duration(since, until).getMillis();
    if (diff <= 1000) {
      return "0 " + l10n.get("TimeUnit.seconds");
    }

    if (diff < MILLIS_IN_MONTH) {
      return timeStringSmallScale(new Period(since, until, PeriodType.dayTime()));
    } else {
      return timeStringLargeScale(new Period(since, until, PeriodType.yearMonthDay()));
    }
  }

  /**
   * Internal method to convert a period of lesser than month scales
   *
   * @param period The period to format
   * @return A relative time string describing the period
   */
  private String timeStringSmallScale(Period period) {
    PeriodFormatterBuilder builder = new PeriodFormatterBuilder()
        .printZeroNever()
        .appendDays()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_DAY), l10n.getString(TIMEUNIT_DAYS))
        .appendSeparator(", ")
        .appendHours()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_HOUR), l10n.getString(TIMEUNIT_HOURS))
        .appendSeparator(", ")
        .appendMinutes()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_MINUTE), l10n.getString(TIMEUNIT_MINUTES));

    if (period.toStandardHours().getHours() == 0) {
      // Do not append seconds if the period is larger than one hour
      builder.appendSeparator(", ")
          .appendSeconds()
          .appendSuffix(" ")
          .appendSuffix(l10n.getString(TIMEUNIT_SECOND), l10n.getString(TIMEUNIT_SECONDS));
    }

    return builder.toFormatter().print(period);
  }

  /**
   * Internal method to convert a period of greater than month scales
   *
   * @param period The period to format
   * @return A relative time string describing the period
   */
  private String timeStringLargeScale(Period period) {
    return new PeriodFormatterBuilder()
        .printZeroNever()
        .appendYears()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_YEAR), l10n.getString(TIMEUNIT_YEARS))
        .appendSeparator(", ")
        .appendMonths()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_MONTH), l10n.getString(TIMEUNIT_MONTHS))
        .appendSeparator(", ")
        .appendDays()
        .appendSuffix(" ")
        .appendSuffix(l10n.getString(TIMEUNIT_DAY), l10n.getString(TIMEUNIT_DAYS))
        .toFormatter()
        .print(period);
  }
}
