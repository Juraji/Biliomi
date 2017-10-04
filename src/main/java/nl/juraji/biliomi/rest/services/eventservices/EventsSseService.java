package nl.juraji.biliomi.rest.services.eventservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.utility.events.Event;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Juraji on 14-6-2017.
 * Biliomi v3
 */
@Singleton
@Path("/events")
@EventBusSubscriber
public class EventsSseService {
  private final SseBroadcaster sseBroadcaster = new SseBroadcaster();

  @Inject
  private Logger logger;

  @Subscribe
  public void onEvent(Event event) {
    try {
      String eventData = JacksonMarshaller.marshal(event);

      OutboundEvent outboundEvent = new OutboundEvent.Builder()
          .mediaType(MediaType.APPLICATION_JSON_TYPE)
          .data(String.class, eventData)
          .build();

      sseBroadcaster.broadcast(outboundEvent);
    } catch (JsonProcessingException e) {
      logger.error("Failed marshalling event for REST SSE events", e);
    }
  }

  @GET
  @Produces(SseFeature.SERVER_SENT_EVENTS)
  public EventOutput listenToEvents() {
    final EventOutput eventOutput = new EventOutput();
    sseBroadcaster.add(eventOutput);
    return eventOutput;
  }
}
