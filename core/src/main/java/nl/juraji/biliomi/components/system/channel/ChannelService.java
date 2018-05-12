package nl.juraji.biliomi.components.system.channel;

import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.components.system.users.UsersService;
import nl.juraji.biliomi.io.api.twitch.v5.TwitchApi;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchGame;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchStream;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.TwitchStreamInfo;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.internal.events.twitch.webhook.ChannelStateEvent;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 22-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
@EventBusSubscriber
public class ChannelService {

    private boolean streamOnline = false;

    @Inject
    private Logger logger;

    @Inject
    private TwitchApi twitchApi;

    @Inject
    private UsersService usersService;

    @Inject
    private GameService gameService;

    @PostConstruct
    private void initChannelService() {
        // When Biliomi initially boots the stream status will be unknown.
        // This will set the status using the Api, just in case Biliomi is booted during stream.
        streamOnline = getStream() != null;
    }

    @Subscribe
    public void onChannelStateEvent(ChannelStateEvent event) {
        streamOnline = event.isOnline();
        logger.info("Stream went {}", (streamOnline ? "online" : "offline"));
    }

    /**
     * Get the channel id of the channel Biliomi is currently connected to
     *
     * @return A Twitch id for the current channel
     */
    public long getChannelId() {
        return usersService.getCaster().getTwitchUserId();
    }

    /**
     * Get current channel online/offline status
     *
     * @return True if the current channel is online else False
     */
    public boolean isStreamOnline() {
        return streamOnline;
    }

    /**
     * Get stream information on the current channel
     *
     * @return A TwitchStream object if the request succeeded else null
     */
    public TwitchStream getStream() {
        return getStream(getChannelId());
    }

    /**
     * Get the stream for a specific Twitch Channel
     *
     * @param twitchId The Twitch id of which to fetch the stream information
     * @return A TwitchStream object if the request succeeded else null
     */
    public TwitchStream getStream(long twitchId) {
        try {
            Response<TwitchStreamInfo> streams = twitchApi.getStream(twitchId);
            if (streams.isOK()) {
                return streams.getData().getStream();
            }
        } catch (Exception e) {
            logger.error("Error retrieving stream information for channel " + twitchId, e);
        }

        return null;
    }

    /**
     * Get channel information on the current channel
     *
     * @return The TwitchChannel or null on failure
     */
    public TwitchChannel getChannel() {
        return getChannel(getChannelId());
    }

    /**
     * Get channel information
     *
     * @param twitchId The channel's Twitch id
     * @return The TwitchChannel or null on failure
     */
    public TwitchChannel getChannel(long twitchId) {
        try {
            Response<TwitchChannel> channel = twitchApi.getChannel(twitchId);

            if (channel.isOK() && channel.getData() != null) {
                return channel.getData();
            }
        } catch (Exception e) {
            logger.error("Error retrieving channel information for channel " + twitchId, e);
        }
        return null;
    }

    /**
     * Get the current channel status
     *
     * @return The current channel status or null on request failed
     */
    public String getCurrentStatus() {
        try {
            Response<TwitchChannel> channel = twitchApi.getChannel();

            if (channel.isOK()) {
                return channel.getData().getStatus();
            }
        } catch (Exception e) {
            logger.error("Error retrieving channel information for caster channel", e);
        }

        return null;
    }

    /**
     * Update the game of the current channel
     *
     * @param gameName The game name to set
     * @return The game name that was used in the update (Might be different due to game lookup) or null on failure
     */
    public Game updateGame(String gameName) {
        long channelId = usersService.getCaster().getTwitchUserId();

        try {
            TwitchGame twitchGame = twitchApi.searchGame(gameName);
            Response<TwitchChannel> response = twitchApi.updateChannel(channelId, twitchGame.getName(), null);

            if (response.isOK()) {
                return gameService.getByName(response.getData().getGame());
            }
        } catch (Exception e) {
            logger.error("Error updating channel game for " + channelId, e);
        }

        return null;
    }

    public boolean updateStatus(String newstatus) {
        long channelId = usersService.getCaster().getTwitchUserId();

        try {
            Response<TwitchChannel> response = twitchApi.updateChannel(channelId, null, newstatus);
            return response.isOK() && response.getData() != null;
        } catch (Exception e) {
            logger.error("Error updating channel status for " + channelId, e);
        }

        return false;
    }
}
