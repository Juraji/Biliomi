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
import nl.juraji.biliomi.model.internal.rest.ChannelStatus;
import nl.juraji.biliomi.model.internal.rest.PaginatedResponse;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.types.Templater;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        boolean streamOnline = channelService.isStreamOnline();
        TwitchStream stream = null;
        TwitchChannel channel;
        ChannelStatus status = null;

        if (streamOnline) {
            stream = channelService.getStream();
            channel = stream.getChannel();
        } else {
            channel = channelService.getChannel();
        }

        if (channel != null) {
            status = new ChannelStatus();
            Template statusTemplate = templateDao.getByKey(ChannelSettingsComponent.CHANNEL_TITLE_TEMPLATE_KEY);
            String statusWithoutTemplate = Templater.removeTemplate(channel.getStatus(), statusTemplate.getTemplate());

            status.setChannelName(channel.getDisplayName());
            status.setFollowerCount(channel.getFollowers());
            status.setSubscriberCount(usersService.getSubscriberCount());
            status.setGame(gameService.getByName(channel.getGame()));
            status.setStatus(channel.getStatus());
            status.setStatusWithoutTemplate(statusWithoutTemplate);
            status.setLogoUri(channel.getLogo());
            status.setAffiliate(status.getSubscriberCount() > 0 || channel.isPartner());
            status.setPartner(channel.isPartner());

            if (stream != null) {
                status.setOnline(true);
                status.setPreviewUri(stream.getPreview().getLarge());
            }
        }

        return Responses.okOrEmpty(status);
    }

    @GET
    @Path("/viewers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelViewers() {
        return PaginatedResponse.create(chatService.getViewersAsUsers());
    }
}
