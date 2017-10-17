package nl.juraji.biliomi.io.api.twitch.pubsub;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.message.PubSubMessageData;
import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubTopic;
import nl.juraji.biliomi.model.internal.events.twitch.bits.TwitchBitsEvent;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.SubscriberPlanType;
import nl.juraji.biliomi.model.internal.events.twitch.subscribers.TwitchSubscriberEvent;
import nl.juraji.biliomi.utility.events.Event;
import nl.juraji.biliomi.utility.events.EventBus;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

/**
 * Created by Juraji on 4-9-2017.
 * Biliomi v3
 */
public class PubSubClientTest {

  @Captor
  private ArgumentCaptor<TwitchBitsEvent> bitsEventArgumentCaptor;

  @Captor
  private ArgumentCaptor<TwitchSubscriberEvent> subscriberEventArgumentCaptor;

  @Mock
  private EventBus eventBus;

  private PubSubClient client;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.doNothing().when(eventBus).post(any(Event.class));

    client = new PubSubClient(eventBus, 0, "");
  }

  @Test
  public void pubSubTopicDeserialization() throws Exception {
    String json = "{\"topic\": \"channel-commerce-events-v1.44322889\", \"data\": \"\"}";

    PubSubMessageData data = JacksonMarshaller.unmarshal(json, PubSubMessageData.class);
    assertEquals(PubSubTopic.COMMERCE, data.getTopic());
  }

  @Test
  public void pubSubMessageDeserializationForBits() throws Exception {
    String json = getJson("pubsub_bits_event");

    client.onTextMessage(null, json);
    Mockito.verify(eventBus).post(bitsEventArgumentCaptor.capture());
    TwitchBitsEvent event = bitsEventArgumentCaptor.getValue();

    assertEquals("dallasnchains", event.getUsername());
    assertEquals(10000, event.getBitsUsed());
  }

  @Test
  public void pubSubMessageDeserializationForForSubscription() throws Exception {
    String json = getJson("pubsub_subscription_event");

    client.onTextMessage(null, json);
    Mockito.verify(eventBus).post(subscriberEventArgumentCaptor.capture());
    TwitchSubscriberEvent event = subscriberEventArgumentCaptor.getValue();

    assertEquals("dallas", event.getUsername());
    assertEquals(SubscriberPlanType.TIER2, event.getSubPlan());
    assertTrue(event.isResub());
  }

  private String getJson(String name) throws IOException {
    InputStream jsonResourceStream = PubSubClientTest.class.getResourceAsStream("/api/twitch/pubsub/"+name+".json");
    return IOUtils.toString(jsonResourceStream, "UTF-8");
  }
}