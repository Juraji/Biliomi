package nl.juraji.biliomi.io.api.twitch.v5;

import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchChannel;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchCommunity;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchGame;
import nl.juraji.biliomi.io.api.twitch.v5.model.TwitchUser;
import nl.juraji.biliomi.io.api.twitch.v5.model.wrappers.*;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.model.core.Community;

import java.util.Set;

/**
 * Created by Juraji on 20-4-2017.
 * Biliomi v3
 */
public interface TwitchApi {

    /**
     * Retrieves a channel object, corresponding to the caster oAuth token
     *
     * @return A Response containing a TwitchChannel object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#get-channel">Twitch API docs</a>
     */
    Response<TwitchChannel> getChannel() throws Exception;

    /**
     * Retrieves a channel object, corresponding to twitchId
     *
     * @param twitchId The Twitch id of the channel to retrieve
     * @return A Response containing a TwitchChannel object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#get-channel-by-id">Twitch API docs</a>
     */
    Response<TwitchChannel> getChannel(long twitchId) throws Exception;

    /**
     * Update a channel using the given information
     *
     * @param twitchId The user id of the channel to update
     * @param game     (Optional) The new game to set
     * @param status   (Optional) The new channel status (title) to set
     * @return A Response containing a TwitchChannel object or null on failure
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#update-channel">Twitch API docs</a>
     */
    Response<TwitchChannel> updateChannel(long twitchId, String game, String status) throws Exception;

    /**
     * Retrieves a list of followers for the given twitchid's channel
     *
     * @param twitchId The user id of the channel to fetch the followers for
     * @param limit    The maximum of results to retrieve (max 100)
     * @param offset   The offset from 0
     * @return A Response containing a TwitchFollows object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#get-channel-followers">Twitch API docs</a>
     */
    Response<TwitchFollows> getChannelFollowers(long twitchId, int limit, int offset) throws Exception;

    /**
     * Retrieves a list of subscriptions for the given twitchid's channel
     *
     * @param twitchId The user id of the channel to fetch the subscriptions for
     * @param limit    The maximum of results to retrieve (max 100)
     * @param offset   The offset from 0
     * @return A Response with TwitchSubscriptions object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#get-channel-subscribers">Twitch API docs</a>
     */
    Response<TwitchSubscriptions> getChannelSubscriptions(long twitchId, int limit, int offset) throws Exception;

    /**
     * Retrieves a list of teams for the given twitchid's channel
     *
     * @param twitchId The user id of the channel to fetch the teams for
     * @return A Response with TwitchTeams object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels#get-channel-teams">Twitch API docs</a>
     */
    Response<TwitchTeams> getChannelTeams(long twitchId) throws Exception;

    /**
     * Retrieves a single community by its name
     *
     * @param communityName The name of the community to search for
     * @return A Response with a TwitchCommunty object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/communities#get-community-by-name">Get Community by Name</a>
     */
    Response<TwitchCommunity> getCommunityByName(String communityName) throws Exception;

    /**
     * Retrieves a list of communities the channel is currently in
     *
     * @param twitchId The user id of the channel to fetch communities for
     * @return A Response with TwitchCommunities object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/channels/#get-channel-communities">Twitch API docs</a>
     */
    Response<TwitchCommunities> getChannelCommunities(long twitchId) throws Exception;

    /**
     * Set communities for the current channel (max 3)
     *
     * @param communities A set of 3 communities at maximum
     * @return A Response with no object
     */
    Response<Void> updateChannelCommunities(long twitchId, Set<Community> communities) throws Exception;

    /**
     * Remove a community from a channel
     *
     * @param twitchId The user id of the channel to fetch communities for
     * @return A Response with no object
     */
    Response<Void> clearChannelCommunities(long twitchId) throws Exception;

    /**
     * Retrieve current streams of a Twitch user
     *
     * @param twitchId The user id of the channel to fetch the streams for
     * @return A Response with TwitchStreamInfo object
     * @see <a href="https://dev.twitch.tv/docs/v5/reference/streams/#get-stream-by-user">Twitch API docs</a>
     */
    Response<TwitchStreamInfo> getStream(long twitchId) throws Exception;

    /**
     * Translate usernames to TwitchUser objects
     *
     * @param usernames The usernames to look up
     * @return A Response with TwitchUserLogins object containing the matches returned by the Twitch Api
     * @see <a href="https://dev.twitch.tv/docs/v5/guides/using-the-twitch-api/#translating-from-user-names-to-user-ids">Twitch API docs</a>
     */
    Response<TwitchUserLogins> getUsersByUsername(String... usernames) throws Exception;

    /**
     * Retrieve a Twitch user corresponding to the given user id
     *
     * @param twitchId The user id to look up
     * @return A Response with TwitchUser object
     * @see <a href="https://dev.twitch.tv/docs/v5/guides/using-the-twitch-api/#get-user-by-id">Twitch API docs</a>
     */
    Response<TwitchUser> getUser(String twitchId) throws Exception;

    /**
     * UNOFFICIAL
     * Fetch channels currently hosting the target channel
     *
     * @param twitchId The user id of the channel to fetch the hosts for
     * @return A Response with TmiHosts object
     */
    Response<TmiHosts> getHostUsers(String twitchId) throws Exception;

    /**
     * Search the Twitch api for the given name
     *
     * @param gameName The name of the game to look up
     * @return A TwitchGame object, if the game is not found in the api only the name will be set
     * @see <a href="https://dev.twitch.tv/docs/v5/guides/using-the-twitch-api/#search-games">Twitch API docs</a>
     */
    TwitchGame searchGame(String gameName) throws Exception;
}
