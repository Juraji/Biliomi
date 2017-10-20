package nl.juraji.biliomi.components.chat.pms;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.io.api.twitch.irc.IrcSession;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcPrivateMessageEvent;
import nl.juraji.biliomi.utility.calculate.TextUtils;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

/**
 * Created by Juraji on 26-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
@EventBusSubscriber
public class PMResponseComponent extends Component {
  public static final String PM_RESPONSE_TEMPLATE_ID = "PMResponse";

  @Inject
  private TemplateDao templateDao;

  @Subscribe
  public void onIrcPrivateMessageEvent(IrcPrivateMessageEvent event) {
    if (event.getUsername().equalsIgnoreCase(IrcSession.SYSTEM_USER)) {
      return;
    }

    Template template = templateDao.getByKey(PM_RESPONSE_TEMPLATE_ID);
    if (template == null || template.getTemplate() == null) {
      return;
    }
    chat.whisper(event.getUsername(), Templater.template(template.getTemplate())
        .add("moderators", this::createModeratorList));
  }

  private String createModeratorList() {
    Stream<String> moderators = usersService.getModerators().stream().map(User::getDisplayName);
    return TextUtils.commaList(moderators);
  }
}
