package nl.juraji.biliomi.rest.services.rest.info;

import nl.juraji.biliomi.components.shared.ChatService;
import nl.juraji.biliomi.components.system.channel.GameService;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TmiHost;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchImageInfo;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TmiHosts;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchStreamInfo;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.model.internal.rest.ChannelInfo;
import nl.juraji.biliomi.rest.config.Responses;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Juraji on 25-6-2017.
 * Biliomi v3
 */
@Path("/info")
public class InfoService {

  @Inject
  private TwitchApi twitchApi;

  @Inject
  private GameService gameService;

  @Inject
  private ChatService chatService;

  @Inject
  private UsersService usersService;

  @Inject
  private VersionInfo versionInfo;

  @GET
  @Path("/version")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getVersionInfo() {
    return Responses.ok(versionInfo);
  }


  @GET
  @Path("/channel")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getChannelStatus() throws Exception {
    ChannelInfo info = null;
    nl.juraji.biliomi.io.web.Response<TwitchChannel> channelResponse = twitchApi.getChannel();

    if (channelResponse.isOK()) {
      TwitchChannel channel = channelResponse.getData();
      info = new ChannelInfo();

      info.setChannelName(channel.getDisplayName());
      info.setFollowerCount(channel.getFollowers());
      info.setSubscriberCount(usersService.getSubscriberCount());
      info.setGame(gameService.getByName(channel.getGame(), true));
      info.setStatus(channel.getStatus());
      info.setLogoUri(channel.getLogo());
      info.setAffiliate(info.getSubscriberCount() > 0 || channel.isPartner());
      info.setPartner(channel.isPartner());
      info.getViewers().addAll(chatService.getViewersAsUsers());

      insertTwitchStreamInfo(info, channel);
      insertTwitchHostUsers(info, channel);
    }

    return Responses.okOrEmpty(info);
  }

  private void insertTwitchStreamInfo(ChannelInfo info, TwitchChannel channel) throws Exception {
    nl.juraji.biliomi.io.web.Response<TwitchStreamInfo> streamResponse = twitchApi.getStream(channel.getId());
    if (streamResponse.isOK()) {
      TwitchStreamInfo streamInfo = streamResponse.getData();
      if (streamInfo.getStream() != null) {
        info.setOnline(true);

        TwitchImageInfo preview = streamInfo.getStream().getPreview();
        info.setPreviewUri(preview.getMedium());
      }
    }
  }

  private void insertTwitchHostUsers(ChannelInfo info, TwitchChannel channel) throws Exception {
    nl.juraji.biliomi.io.web.Response<TmiHosts> hostsResponse = twitchApi.getHostUsers(String.valueOf(channel.getId()));
    if (hostsResponse.isOK()) {
      TmiHosts tmiHosts = hostsResponse.getData();
      if (tmiHosts.getHosts() != null) {
        List<User> collect = tmiHosts.getHosts().stream()
            .map(TmiHost::getHostUsername)
            .map(hostId -> usersService.getUser(hostId, true))
            .collect(Collectors.toList());

        info.getHosters().addAll(collect);
      }
    }
  }
}
