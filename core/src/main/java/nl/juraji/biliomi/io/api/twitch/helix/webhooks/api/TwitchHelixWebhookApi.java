package nl.juraji.biliomi.io.api.twitch.helix.webhooks.api;

import nl.juraji.biliomi.io.web.Response;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
public interface TwitchHelixWebhookApi {

    /**
     * Subscribe to incoming followers using the Helix webhook
     *
     * @param localEndpoint        The local endpoint to append to the callback url
     * @param leaseDurationSeconds The duration of the subscription in seconds
     * @param targetChannelId      The Twitch id of the channel for which to subscribe to new follower events
     * @return An empty response with status 200 if ok else not.
     * @see <a href="https://dev.twitch.tv/docs/api/webhooks-reference#topic-user-follows">Topic: User Follows</a>
     */
    Response<Void> subscribeToStreamsWebhookTopic(String localEndpoint, long leaseDurationSeconds, String targetChannelId) throws Exception;

    /**
     * Subscribe to incoming followers using the Helix webhook
     *
     * @param localEndpoint        The local endpoint to append to the callback url
     * @param leaseDurationSeconds The duration of the subscription in seconds
     * @param targetChannelId      The Twitch id of the channel for which to subscribe to new follower events
     * @return An empty response with status 200 if ok else not
     * @see <a href="https://dev.twitch.tv/docs/api/webhooks-reference#topic-stream-updown">Topic: Stream Up/Down</a>
     */
    Response<Void> subscribeToFollowersWebhookTopic(String localEndpoint, long leaseDurationSeconds, String targetChannelId) throws Exception;
}
