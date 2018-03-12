package nl.juraji.biliomi.components.system.users;

import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import nl.juraji.biliomi.io.api.twitch.irc.utils.Tags;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.irc.channel.IrcChannelNoticeEvent;
import nl.juraji.biliomi.utility.calculate.TextUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.BotName;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.ChannelName;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.components.TimerService;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by robin.
 * april 2017
 */
@Default
@Singleton
@EventBusSubscriber
public class ModWatchTimerService extends TimerService {
  private static final long UPDATE_INTERVAL = 12;
  private static final TimeUnit UPDATE_INTERVAL_TU = TimeUnit.HOURS;

  @Inject
  @BotName
  private String botName;

  @Inject
  @ChannelName
  private String channelName;

  @Inject
  private IrcSession session;

  @Inject
  private UsersService usersService;

  @Override
  public void start() {
    super.start();
    schedule(this::updateNow, 30, TimeUnit.SECONDS);
    scheduleAtFixedRate(this::updateNow, UPDATE_INTERVAL, UPDATE_INTERVAL, UPDATE_INTERVAL_TU);
  }

  @Subscribe
  public void onIrcChannelNoticeEvent(IrcChannelNoticeEvent event) {
    if (Tags.MsgId.ROOM_MODS.equals(event.getMsgId())) {

      // Make sure the bot's username is in the list
      passOrFailBotIsMod(event.getMessage());
      List<User> updatedUsers = new ArrayList<>();

      // The message starts of with "The moderators of this room are: ", this bit needs to be taken out.
      List<String> moderatorUsernames = Splitter.on(Pattern.compile("[:,] ")).splitToList(event.getMessage());
      moderatorUsernames.remove(0);

      // Check each user in the list agains the corresponding users in the database.
      // Set moderator to true and add all users to the updatedUsers list if they aren't known as moderator already
      moderatorUsernames.stream()
          .map(username -> usersService.getUser(username))
          .filter(user -> !user.isModerator())
          .forEach(user -> {
            user.setModerator(true);
            updatedUsers.add(user);
          });

      // Get the currently known moderators and check if they are present in the list.
      // If not set moderator to false and add them to the updatedUsers list
      List<User> botModerators = usersService.getModerators();
      botModerators.stream()
          .filter(user -> !user.getUsername().equals(botName))
          .filter(user -> !moderatorUsernames.contains(user.getUsername()))
          .forEach(user -> {
            user.setModerator(false);
            updatedUsers.add(user);
          });

      // Save all user objects in the updatedUsers list
      usersService.save(updatedUsers);
      logger.info("Current moderators: {}", TextUtils.commaList(moderatorUsernames));
    }
  }

  private void passOrFailBotIsMod(String message) {
    if (!message.contains(botName)) {
      logger.error("The bot user {} is not a moderator in channel #{}! To enable this type the following in the chat: \"/mod {}\"", botName, channelName, botName);
      BiliomiContainer.getContainer().shutdownInError();
    }
  }

  public void updateNow() {
    session.getChatClient().say(".mods");
  }
}
