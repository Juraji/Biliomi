package nl.juraji.biliomi.io.api.twitch.helix.webhooks.api;

import com.google.common.net.MediaType;
import nl.juraji.biliomi.io.web.Response;
import nl.juraji.biliomi.io.web.Url;
import nl.juraji.biliomi.io.web.WebClient;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.CoreSetting;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 2-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class TwitchHelixWebhookApiImpl implements TwitchHelixWebhookApi {
  private final HttpFields headers = new HttpFields();

  @Inject
  @AppData("apis.twitch.helix.baseUri")
  private String apiBaseUri;

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
    Url hubTopic = Url.url(apiBaseUri, "streams")
        .withQueryParam("user_id", targetChannelId);

    Url url = Url.url(apiBaseUri, "webhooks", "hub")
        .withQueryParam("hub.callback", webhookCallbackUrl + localEndpoint)
        .withQueryParam("hub.mode", "subscribe")
        .withQueryParam("hub.topic", hubTopic)
        .withQueryParam("hub.lease_seconds", leaseDurationSeconds);

    return webClient.post(url, headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }

  @Override
  public Response<Void> subscribeToFollowersWebhookTopic(String localEndpoint, long leaseDurationSeconds, String targetChannelId) throws Exception {
    Url hubTopic = Url.url(apiBaseUri, "users", "follows")
        .withQueryParam("to_id", targetChannelId);

    Url url = Url.url(apiBaseUri, "webhooks", "hub")
        .withQueryParam("hub.callback", webhookCallbackUrl + localEndpoint)
        .withQueryParam("hub.mode", "subscribe")
        .withQueryParam("hub.topic", hubTopic)
        .withQueryParam("hub.lease_seconds", leaseDurationSeconds);

    return webClient.post(url, headers, "", MediaType.PLAIN_TEXT_UTF_8, Void.class);
  }
}
