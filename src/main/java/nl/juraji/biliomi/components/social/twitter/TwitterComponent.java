package nl.juraji.biliomi.components.social.twitter;

import nl.juraji.biliomi.components.shared.TemplateSetup;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchStream;
import nl.juraji.biliomi.io.api.twitter.v1.TwitterApi;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.social.twitter.TwitterSettings;
import nl.juraji.biliomi.components.interfaces.Component;
import nl.juraji.biliomi.components.interfaces.enums.OnOff;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.utility.calculate.EnumUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.CliCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.exceptions.UnavailableException;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Juraji on 11-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class TwitterComponent extends Component {
  public static final String TWITTER_UPDATE_TEMPLATE_ID = "TwitterStatusUpdate";
  public static final String TWITTER_TWEET_FOUND_TEMPLATE_ID = "TwitterTweetFound";
  private static final int TRACKED_WORD_MIN_LENGTH = 6;

  @Inject
  @BotName
  private String botName;

  @Inject
  private TwitterApi twitterApi;

  @Inject
  private TweetTrackerService tweetTrackerService;

  @Inject
  private SettingsService settingsService;

  @Inject
  private TemplateDao templateDao;

  @Inject
  private ChannelService channelService;

  @Override
  public void init() {
    tweetTrackerService.start();
  }

  @PreDestroy
  private void stopTwitterComponent() {
    tweetTrackerService.stop();
  }

  /**
   * Update Twitter
   * Usage: !tweetstatus [status... or nothing to use the update template]
   */
  @CommandRoute(command = "tweetstatus", systemCommand = true, modCanActivate = true)
  public boolean tweetStatusCommand(User user, Arguments arguments) {
    String customStatus = arguments.toString();

    try {
      if (StringUtils.isNotEmpty(customStatus)) {
        // User supplied a message to tweet
        twitterApi.postTweet(customStatus);
      } else {
        // User did not supply a message, use the template instead
        Template template = templateDao.getByKey(TWITTER_UPDATE_TEMPLATE_ID);
        TwitchStream stream = channelService.getStream();

        if (stream == null) {
          chat.whisper(user, l10n.get("ChatCommand.tweetStatus.channelOffline"));
          return false;
        }


        assert template != null; // Template cannot be null since it's set during install/update
        twitterApi.postTweet(Templater.template(template.getTemplate())
            .add("channel", usersService.getCaster()::getDisplayName)
            .add("botname", botName)
            .add("game", stream::getGame)
            .add("status", stream.getChannel()::getStatus)
            .apply());
      }
    } catch (UnavailableException e) {
      chat.whisper(user, l10n.get("ChatCommand.tweetStatus.twitterNotAvailable"));
      return false;
    } catch (Exception e) {
      logger.error("Error posting twitter update", e);
      return false;
    }

    return true;
  }

  /**
   * The main command for setting up how Biliomi uses Twitter
   * Usage: !twitter [setupdatetemplate|settrackedwords] [more...]
   */
  @CommandRoute(command = "twitter", systemCommand = true)
  public boolean twitterCommand(User user, Arguments arguments) {
    return captureSubCommands("twitter", () -> l10n.getString("ChatCommand.twitter.usage"), user, arguments);
  }

  /**
   * Set the template to use when posting an update to Twitter
   * Usage: !twitter setupdatetemplate [message...]
   */
  @SubCommandRoute(parentCommand = "twitter", command = "setupdatetemplate")
  public boolean twitterSetUpdateTemplateCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(l10n.getString("ChatCommand.twitter.setupdatetemplate.usage"))
        .withTemplatedSavedMessage(l10n.getString("ChatCommand.twitter.setupdatetemplate.set"))
        .apply(user, arguments.toString(), TWITTER_UPDATE_TEMPLATE_ID);
  }

  /**
   * Set words to track on Twitter
   * Warning: Be sure to use specific words, common words might overload Biliomi and cause Biliomi to stop listening for tweets
   * Usage: !twitter settrackedwords [tracked words... or OFF to disable]
   */
  @SubCommandRoute(parentCommand = "twitter", command = "settrackedwords")
  public boolean twitterSetTrackedWordsCommand(User user, Arguments arguments) {
    if (arguments.isEmpty()) {
      chat.whisper(user, l10n.get("ChatCommand.twitter.settrackedwords.usage"));
      return false;
    }

    List<String> words = Arrays.asList(arguments.getArguments());
    OnOff onOff = EnumUtils.toEnum(words.get(0), OnOff.class);

    TwitterSettings settings = settingsService.getSettings(TwitterSettings.class);
    settings.getTrackedKeywords().clear();

    if (OnOff.OFF.equals(onOff)) {
      chat.whisper(user, l10n.get("ChatCommand.twitter.settrackedwords.disabled"));
    } else {
      boolean wordlengthViolated = words.stream().anyMatch(s -> s.length() < TRACKED_WORD_MIN_LENGTH);
      if (wordlengthViolated) {
        chat.whisper(user, l10n.get("ChatCommand.twitter.settrackedwords.minWordLengthViolated")
            .add("min", TRACKED_WORD_MIN_LENGTH));
        return false;
      }
      settings.getTrackedKeywords().addAll(words);
      chat.whisper(user, l10n.get("ChatCommand.twitter.settrackedwords.set")
          .add("words", settings::getTrackedKeywords));
    }

    settingsService.save(settings);
    tweetTrackerService.restart();
    return true;
  }

  /**
   * Set the template to post in the chat when a tracked word is matched
   * Usage: !twitter setwordmatchedtemplate [template...]
   */
  @SubCommandRoute(parentCommand = "twitter", command = "setwordmatchedtemplate")
  public boolean twitterSetWordMatchedTemplateCommand(User user, Arguments arguments) {
    return new TemplateSetup(templateDao, chat)
        .withCommandUsageMessage(l10n.getString("ChatCommand.twitter.setwordmatchedtemplate.usage"))
        .withTemplatedSavedMessage(l10n.getString("ChatCommand.twitter.setwordmatchedtemplate.set"))
        .apply(user, arguments.toString(), TWITTER_TWEET_FOUND_TEMPLATE_ID);
  }

  @CliCommandRoute(command = "tweettrackerstatus", description = "Get information on the tweet tracker")
  public boolean tweetTrackerStatusCommand(ConsoleInputEvent e) {
    String status = tweetTrackerService.getStatus();
    logger.info(status);

    return true;
  }
}
