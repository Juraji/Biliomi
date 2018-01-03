package nl.juraji.biliomi.io.api.twitch.helix.webhooks.api;

import com.google.common.net.MediaType;
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

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class TwitchHelixWebhookApiImpl implements TwitchHelixWebhookApi {
  private static final String API_BASE_URI = "https://api.twitch.tv/helix";
  private final HttpFields headers = new HttpFields();

  @Inject
  @CoreSetting("biliomi.twitch.clientId")
  private String clientId;

  @Inject
  @CoreSetting("biliomi.twitch.webhookCallbackUrl")
  private String webhookCallbackUrl;

  @Inject
  private WebClient webClient;

  @PostConstruct
  public void initTwitchApi() {
    headers.put("Client-ID", clientId);
    headers.put(HttpHeader.ACCEPT, "application/vnd.twitchtv.v5+json");
  }

  @Override
  public Response<Void> subscribeToStreamsWebhookTopic(String localEndpoint, long leaseDurationSeconds, String targetChannelId) throws Exception {
    HashMap<String, Object> topicQuery = new HashMap<>();
    topicQuery.put("user_id", targetChannelId);

    HashMap<String, Object> query = new HashMap<>();
    query.put("hub.callback", webhookCallbackUrl + localEndpoint);
    query.put("hub.mode", "subscribe");
    query.put("hub.topic", Url.url(API_BASE_URI, "streams").withQuery(topicQuery));
    query.put("hub.lease_seconds", leaseDurationSeconds);

    return webClient.post(Url.url(API_BASE_URI, "webhooks", "hub").withQuery(query), headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }

  @Override
  public Response<Void> subscribeToFollowersWebhookTopic(String localEndpoint, long leaseDurationSeconds, String targetChannelId) throws Exception {
    HashMap<String, Object> topicQuery = new HashMap<>();
    topicQuery.put("to_id", targetChannelId);

    HashMap<String, Object> query = new HashMap<>();
    query.put("hub.callback", webhookCallbackUrl + localEndpoint);
    query.put("hub.mode", "subscribe");
    query.put("hub.topic", Url.url(API_BASE_URI, "users", "follows").withQuery(topicQuery));
    query.put("hub.lease_seconds", leaseDurationSeconds);

    return webClient.post(Url.url(API_BASE_URI, "webhooks", "hub").withQuery(query), headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }
}
