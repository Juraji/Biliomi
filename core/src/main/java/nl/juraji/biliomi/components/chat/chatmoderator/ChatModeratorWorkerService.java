package nl.juraji.biliomi.components.chat.chatmoderator;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.chat.ModerationAction;
import nl.juraji.biliomi.model.chat.ModerationReason;
import nl.juraji.biliomi.model.chat.settings.ChatModeratorSettings;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.utility.calculate.PatternUtils;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.L10nData;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.collections.L10nMap;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static nl.juraji.biliomi.model.chat.ModerationReason.*;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class ChatModeratorWorkerService implements Init {
  private static final String L10N_MODERATION_ACTION_PREFIX = "Moderation.action.";
  private static final String L10N_MODERATION_WARN_MESSAGE_PREFIX = "Moderation.warn.";
  private static final String L10N_MODERATION_ACTION_MESSAGE_PREFIX = "Moderation.messageFor.";

  @Inject
  @L10nData(ChatModeratorComponent.class)
  private L10nMap l10n;

  @Inject
  private UsersService usersService;

  @Inject
  private ChatService chatService;

  @Inject
  private ModerationRecordService recordService;

  @Inject
  private ChatModeratorLinkPermitService linkPermitService;

  @Inject
  private ChatModeratorTallyService tallyService;

  @Inject
  private SettingsService settingsService;
  private ChatModeratorSettings settings;

  @Override
  public void init() {
    if (settings == null) {
      this.settings = settingsService.getSettings(ChatModeratorSettings.class, s -> this.settings = s);
    }

    linkPermitService.start();
    tallyService.start();
  }

  @PreDestroy
  private void destroy() {
    linkPermitService.stop();
    tallyService.stop();
  }

  @Subscribe
  public void onIrcChatMessageEvent(IrcChatMessageEvent event) {
    String username = event.getUsername();
    User user = usersService.getUser(username);

    // Proceed if user not is exempted
    if (!userExemptedByTypeOrGroup(user)) {
      // Run all moderations
      // As opposed to v1 behaviour, each violation now counts as a seperate strike

      StrikeExecutor strikeExecutor = new StrikeExecutor(event);

      moderateLink(strikeExecutor);
      moderateCaps(strikeExecutor);
      moderateRepeatedCharacters(strikeExecutor);

      strikeExecutor.execute();
    }
  }

  private void moderateLink(StrikeExecutor strikeExecutor) {
    // Moderate links if links are not allowed and the message contains links
    if (!settings.isLinksAllowed()) {
      List<String> links = PatternUtils.getLinks(strikeExecutor.getMessage());

      // Proceed if links are found
      // Proceed if User does not have a permit
      // Proceed if one or more links are not whitelisted
      if (!links.isEmpty() && !linkPermitService.usePermit(strikeExecutor.getUsername()) && !allWhitelisted(links)) {
        strikeExecutor.addStrike(POSTED_LINK);
      }
    }
  }

  private void moderateCaps(StrikeExecutor strikeExecutor) {
    // Moderate excessive caps usage if not allowed
    if (!settings.isExcessiveCapsAllowed()) {
      double trigger = settings.getCapsTrigger();
      long length = strikeExecutor.getMessage().length();

      // Proceed if the caps usage count exceeds the trigger count
      if (length >= trigger) {
        long count = strikeExecutor.getMessage().chars().filter(Character::isUpperCase).count();
        double triggerRatio = settings.getCapsTriggerRatio();
        double capsRatio = (double) count / (double) length;

        // Strike if the caps usage ratio exceeds the trigger ratio
        if (capsRatio > triggerRatio) {
          strikeExecutor.addStrike(EXCESSIVE_CAPS);
        }
      }
    }
  }

  private void moderateRepeatedCharacters(StrikeExecutor strikeExecutor) {
    // Moderate excessive repeated characters if not allowed
    if (!settings.isRepeatedCharactersAllowed()) {
      int trigger = settings.getRepeatedCharacterTrigger();
      int longestSequence = PatternUtils.getLongestRepeatedCharacterSequence(strikeExecutor.getMessage());

      // Strike if the longest sequence of characters exceeds the trigger
      if (longestSequence >= trigger) {
        strikeExecutor.addStrike(REPEATED_CHARACTERS);
      }
    }
  }

  private boolean userExemptedByTypeOrGroup(User user) {
    // If the User is null he/she is definetely not exempted
    if (user == null) {
      return false;
    }

    // Caster and moderators are exempted by default
    if (user.isCaster() || user.isModerator()) {
      return true;
    }

    // if a group is set to be exempted then check if the current user is in that group
    return settings.getExemptedGroup() != null && user.getUserGroup().isInGroup(settings.getExemptedGroup());

  }

  private boolean allWhitelisted(List<String> links) {
    List<String> linkWhitelist = settings.getLinkWhitelist();

    // Return false if the whitelist is empty
    if (linkWhitelist.isEmpty()) {
      return false;
    }

    // Check each link against whitelist (found link starts with whitelistedlink)
    // Return True if ALL links atleast start with a whitelisted link
    return links.stream()
        .filter(link -> linkWhitelist.stream().anyMatch(link::startsWith))
        .count() == links.size();
  }

  private class StrikeExecutor {
    private final IrcChatMessageEvent event;
    private ModerationAction action;
    private ModerationReason reason;

    private StrikeExecutor(IrcChatMessageEvent event) {
      this.event = event;
    }

    public String getUsername() {
      return event.getUsername();
    }

    public String getMessage() {
      return event.getMessage();
    }

    public void addStrike(ModerationReason reason) {
      User user = usersService.getUser(event.getUsername(), true);
      int strike = tallyService.tally(user.getUsername());

      this.reason = reason;
      this.action = getStrikeAction(strike);
      recordService.recordModeration(user, reason, action, event.getMessage());
    }

    public void execute() {
      if (action != null) {
        User user = usersService.getUser(event.getUsername(), true);
        int strike = tallyService.getCurrent(user.getUsername());

        executeAction(action, user);

        if (ModerationAction.WARN.equals(action)) {
          chatService.whisper(user, l10n.get(L10N_MODERATION_WARN_MESSAGE_PREFIX + reason.toString())
              .add("strike", strike));
        } else {
          chatService.say(l10n.get(L10N_MODERATION_ACTION_MESSAGE_PREFIX + reason.toString())
              .add("username", user::getDisplayName)
              .add("action", l10n.get(L10N_MODERATION_ACTION_PREFIX + action.toString()).apply())
              .add("strike", strike));
        }
      }
    }

    private ModerationAction getStrikeAction(int strike) {
      if (strike == 1) {
        return settings.getFirstStrike();
      } else if (strike == 2) {
        return settings.getSecondStrike();
      } else {
        return settings.getThirdStrike();
      }
    }

    private void executeAction(ModerationAction action, User user) {
      String username = user.getUsername();

      switch (action) {
        case PURGE:
          chatService.purgeUser(username);
          break;
        case TIMEOUT:
          chatService.timeoutUser(username);
          break;
        case BAN:
          chatService.banUser(username);
          break;
        default:
          break;
      }
    }
  }
}
