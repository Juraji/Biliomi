package nl.juraji.biliomi.components.chat.bits;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.chat.subscribers.SubscriberWatchComponent;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.settings.SettingsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.chat.settings.BitsSettings;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.twitch.bits.TwitchBitsEvent;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.L10nData;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.collections.L10nMap;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by Juraji on 10-9-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class BitsComponentEventService implements Init {

  @Inject
  private TemplateDao templateDao;

  @Inject
  private UsersService usersService;

  @Inject
  private PointsService pointsService;

  @Inject
  private ChatService chat;

  @Inject
  @L10nData(SubscriberWatchComponent.class)
  private L10nMap l10n;

  @Inject
  private SettingsService settingsService;
  private BitsSettings settings;

  @Override
  public void init() {
    settings = settingsService.getSettings(BitsSettings.class, s -> settings = s);
  }

  @Subscribe
  public void onTwitchBitsEvent(TwitchBitsEvent event) {
    User user = usersService.getUserByTwitchId(event.getUserId());

    Template template = templateDao.getByKey(BitsComponentConstants.BITS_CHEERED_TEMPLATE_ID);
    chat.say(Templater.template(template.getTemplate())
        .add("usename", user::getDisplayName)
        .add("amount", event::getBitsUsed));

    bitsToPoints(event, user);
  }

  private void bitsToPoints(TwitchBitsEvent event, User user) {
    if (settings.isEnableBitsToPoints()) {
      long payout = (long) Math.ceil(event.getBitsUsed() * settings.getBitsToPointsMultiplier());

      pointsService.give(user, payout);

      Template template = templateDao.getByKey(BitsComponentConstants.BITS_PAYOUT_TO_CHEERER_TEMPLATE_ID);
      chat.say(Templater.template(template.getTemplate())
          .add("username", user::getDisplayName)
          .add("payout", () -> pointsService.asString(payout)));

      if (settings.isBitsToPointsPayoutToAllChatters()) {
        List<User> viewers = chat.getViewersAsUsers();

        viewers.forEach(viewer -> pointsService.give(viewer, payout));

        template = templateDao.getByKey(BitsComponentConstants.BITS_PAYOUT_TO_CHATTERS_TEMPLATE_ID);
        chat.say(Templater.template(template.getTemplate())
            .add("payout", () -> pointsService.asString(payout)));
      }
    }
  }
}
