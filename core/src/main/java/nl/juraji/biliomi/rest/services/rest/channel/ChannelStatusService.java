package nl.juraji.biliomi.rest.services.rest.channel;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.channel.ChannelService;
import nl.juraji.biliomi.components.system.channel.ChannelSettingsComponent;
import nl.juraji.biliomi.components.system.channel.GameService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchStream;
import nl.juraji.biliomi.model.core.Template;
import nl.juraji.biliomi.model.core.TemplateDao;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.internal.rest.ChannelInfo;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.types.Templater;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Juraji on 25-6-2017.
 * Biliomi v3
 */
@Path("/channel")
public class ChannelStatusService {

  @Inject
  private GameService gameService;

  @Inject
  private ChatService chatService;

  @Inject
  private UsersService usersService;

  @Inject
  private ChannelService channelService;

  @Inject
  private TemplateDao templateDao;

  @GET
  @Path("/status")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChannelStatus() {
    TwitchStream stream = channelService.getStream();
    TwitchChannel channel;
    ChannelInfo info = null;

    if (stream != null) {
      channel = stream.getChannel();
    } else {
      channel = channelService.getChannel();
    }

    if (channel != null) {
      info = new ChannelInfo();
      Template statusTemplate = templateDao.getByKey(ChannelSettingsComponent.CHANNEL_TITLE_TEMPLATE_KEY);
      String statusWithoutTemplate = Templater.removeTemplate(channel.getStatus(), statusTemplate.getTemplate());

      info.setChannelName(channel.getDisplayName());
      info.setFollowerCount(channel.getFollowers());
      info.setSubscriberCount(usersService.getSubscriberCount());
      info.setGame(gameService.getByName(channel.getGame()));
      info.setStatus(channel.getStatus());
      info.setStatusWithoutTemplate(statusWithoutTemplate);
      info.setLogoUri(channel.getLogo());
      info.setAffiliate(info.getSubscriberCount() > 0 || channel.isPartner());
      info.setPartner(channel.isPartner());

      if (stream != null) {
        info.setOnline(true);
        info.setPreviewUri(stream.getPreview().getLarge());
      }
    }

    return Responses.okOrEmpty(info);
  }

  @GET
  @Path("/viewers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChannelViewers() {
    List<User> viewersAsUsers = chatService.getViewersAsUsers();
    PaginatedResponse<User> response = null;

    if (viewersAsUsers.isEmpty()) {
      response = new PaginatedResponse<>();
      response.setEntities(viewersAsUsers);
      response.setTotalAvailable(viewersAsUsers.size());
    }

    return Responses.okOrEmpty(response);
  }
}
