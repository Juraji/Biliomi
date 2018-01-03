package nl.juraji.biliomi.io.api.twitch.helix;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.api.twitch.helix.webhooks.TwitchWebhookSession;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class TwitchHelixApiImpl implements TwitchHelixApi {
  private static final String API_BASE_URI = "https://api.twitch.tv/helix";
  private final HttpFields headers = new HttpFields();

  @Inject
  @CoreSetting("biliomi.twitch.clientId")
  private String clientId;

  @Inject
  @CoreSetting("biliomi.twitch.webhooks.callbackUrl")
  private String webhookCallbackUrl;

  @Inject
  @CoreSetting("biliomi.twitch.webhooks.callbackPort")
  private String webhookCallbackPort;

  @Inject
  private WebClient webClient;

  @PostConstruct
  public void initTwitchApi() {
    headers.put("Client-ID", clientId);
    headers.put(HttpHeader.ACCEPT, "application/vnd.twitchtv.v5+json");
  }

  @Override
  public Response<Void> subscribeToStreamsWebhookTopic(String targetChannelId) throws Exception {
    HashMap<String, Object> topicQuery = new HashMap<>();
    topicQuery.put("user_id", targetChannelId);
    String topicUrl = Url.url(API_BASE_URI, "streams").withQuery(topicQuery).toString();
    Map<String, Object> query = createDefaultWebhookSubscribeQuery(topicUrl, TwitchWebhookSession.STREAMS_ENDPOINT);

    return webClient.post(Url.url(API_BASE_URI, "webhooks", "hub").withQuery(query), headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }

  @Override
  public Response<Void> subscribeToFollowersWebhookTopic(String targetChannelId) throws Exception {
    HashMap<String, Object> topicQuery = new HashMap<>();
    topicQuery.put("to_id", targetChannelId);
    String topicUrl = Url.url(API_BASE_URI, "users", "follows").withQuery(topicQuery).toString();
    Map<String, Object> query = createDefaultWebhookSubscribeQuery(topicUrl, TwitchWebhookSession.FOLLOWS_ENDPOINT);

    return webClient.post(Url.url(API_BASE_URI, "webhooks", "hub").withQuery(query), headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }

  private Map<String, Object> createDefaultWebhookSubscribeQuery(String topicUrl, String callbackEndpoint) {
    HashMap<String, Object> query = new HashMap<>();
    query.put("hub.callback", webhookCallbackUrl + ":" + webhookCallbackPort + callbackEndpoint);
    query.put("hub.mode", "subscribe");
    query.put("hub.topic", topicUrl);
    query.put("hub.lease_seconds", 0);
    query.put("hub.secret", "TEST_SECRET");

    return query;
  }
}
