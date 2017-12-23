package nl.juraji.biliomi.components.chat.announcements;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.model.chat.Announcement;
import nl.juraji.biliomi.model.chat.AnnouncementDao;
import nl.juraji.biliomi.model.chat.AnnouncementsSettings;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.NumberConverter;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class AnnouncementsComponent extends Component {

  @Inject
  private AnnouncementDao announcementDao;

  @Inject
  private AnnouncementTimerService announcementTimerService;

  @Inject
  private TimeFormatter timeFormatter;

  private AnnouncementsSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(AnnouncementsSettings.class, s -> settings = s);
    announcementTimerService.start();
  }

  /**
   * Add/remove announcements and update announcement settings
   * Usage: !announcement [add|remove|interval|minmsgs] [more...]
   */
  @CommandRoute(command = "announcement", systemCommand = true)
  public boolean announcementCommand(User user, Arguments arguments) {
    return captureSubCommands("announcement", i18n.supply("ChatCommand.announcement.usage"), user, arguments);
  }

  /**
   * Add an announcement
   * Usage: !announcement add [message...]
   */
  @SubCommandRoute(parentCommand = "announcement", command = "add")
  public boolean announcementCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertMinSize(1)) {
      chat.whisper(user, i18n.get("ChatCommand.announcement.add.usage"));
      return false;
    }

    String message = arguments.toString();
    announcementDao.create(message);
    announcementTimerService.restart();

    chat.whisper(user, i18n.get("ChatCommand.announcement.add.added")
        .add("message", message));
    return true;
  }

  /**
   * Remove an announcement
   * Usage: !announcement remove [announcement id]
   */
  @SubCommandRoute(parentCommand = "announcement", command = "remove")
  public boolean announcementCommandRemove(User user, Arguments arguments) {
    Long id = NumberConverter.asNumber(arguments.get(0)).toLong();

    if (id == null) {
      chat.whisper(user, i18n.get("ChatCommand.announcement.remove.usage"));
      return false;
    }

    Announcement announcement = announcementDao.get(id);
    if (announcement == null) {
      chat.whisper(user, i18n.get("ChatCommand.announcement.remove.notFound")
          .add("id", id));
      return false;
    }

    announcementDao.delete(announcement);
    announcementTimerService.restart();

    chat.whisper(user, i18n.get("ChatCommand.announcement.remove.deleted")
        .add("message", announcement::getMessage));
    return true;
  }

  /**
   * Set the interval between announcements
   * Usage: !announcement interval [amount of minutes]
   */
  @SubCommandRoute(parentCommand = "announcement", command = "interval")
  public boolean announcementCommandInterval(User user, Arguments arguments) {
    Integer intervalMinutes = NumberConverter.asNumber(arguments.get(0)).toInteger();

    if (intervalMinutes == null || intervalMinutes < 1) {
      chat.whisper(user, i18n.get("ChatCommand.announcement.interval.usage"));
      return false;
    }

    long intervalMillis = TimeUnit.MILLISECONDS.convert(intervalMinutes, TimeUnit.MINUTES);
    settings.setRunInterval(intervalMillis);
    settingsService.save(settings);
    announcementTimerService.restart();

    chat.whisper(user, i18n.get("ChatCommand.announcement.interval.set")
        .add("time", () -> timeFormatter.timeQuantity(settings.getRunInterval())));
    return true;
  }

  /**
   * Set the minimum amount of message in the chat before posting an announcement
   * Usage: !announcement minmsgs [amount of messages]
   */
  @SubCommandRoute(parentCommand = "announcement", command = "minmsgs")
  public boolean announcementCommandMinMsgs(User user, Arguments arguments) {
    Integer minMsgs = NumberConverter.asNumber(arguments.get(0)).toInteger();

    if (minMsgs == null || minMsgs < 0) {
      chat.whisper(user, i18n.get("ChatCommand.announcement.minMsgs.usage"));
      return false;
    }

    settings.setMinChatMessages(minMsgs);
    settingsService.save(settings);

    chat.whisper(user, i18n.get("ChatCommand.announcement.minMsgs.set")
        .add("amount", settings::getMinChatMessages));
    return true;
  }
}
