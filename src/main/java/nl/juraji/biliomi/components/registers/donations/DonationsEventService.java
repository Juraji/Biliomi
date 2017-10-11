package nl.juraji.biliomi.components.registers.donations;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.points.PointsService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.streamlabs.StreamLabsDonationEvent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Init;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import static nl.juraji.biliomi.components.registers.donations.DonationRegisterConstants.*;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
@Default
@Singleton
@EventBusSubscriber
public class DonationsEventService implements Init {

  @Inject
  private DonationsService donationsService;

  @Inject
  private UsersService usersService;

  @Inject
  private PointsService pointsService;

  @Inject
  private ChatService chat;

  @Inject
  private TemplateDao templateDao;

  @Override
  public void init() {
    // Call me
  }

  @Subscribe
  public void onStreamLabsDonationEvent(StreamLabsDonationEvent event) {
    User user = usersService.getUser(event.getUsername());
    if (user != null) {
      double reward = (event.getAmount() * POINTS_MULTIPLIER);
      donationsService.registerDonation(user, event.getFormattedAmount(), event.getMessage());
      pointsService.give(user, reward);

      Template template = templateDao.getByKey(INCOMING_DONATION_NOTICE);
      assert template != null; // Template cannot be null since it's set during install/update
      if (StringUtils.isNotEmpty(template.getTemplate())) {
        chat.say(Templater.template(template.getTemplate())
            .add("username", user::getDisplayName)
            .add("points", pointsService.asString(reward))
            .add("formattedamount", event::getFormattedAmount)
            .add("message", event::getMessage));
      }
    }
  }
}
