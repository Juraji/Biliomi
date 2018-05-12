package nl.juraji.biliomi.rest.services.eventservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.eventbus.Subscribe;
import nl.juraji.biliomi.model.internal.rest.auth.RestAuthorizationResponse;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.utility.events.Event;
import nl.juraji.biliomi.utility.events.interceptors.EventBusSubscriber;
import nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller;
import nl.juraji.biliomi.utility.types.TokenGenerator;
import nl.juraji.biliomi.utility.types.collections.TimedList;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Created by Juraji on 14-6-2017.
 * Biliomi v3
 */
@Singleton
@Path("/events")
@EventBusSubscriber
public class EventsSseService {
    private final TimedList<String> SLTRegister = new TimedList<>("EventsSseService-SLTRegister");
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
    @PermitAll
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput listenToEvents(@QueryParam("token") String token) {
        String removedToken = SLTRegister.remove(token);

        if (removedToken == null) {
            return null;
        }

        final EventOutput eventOutput = new EventOutput();
        sseBroadcaster.add(eventOutput);
        return eventOutput;
    }

    @GET
    @Path("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShortLivedToken() {
        String SLT = new TokenGenerator(20, false).generate();
        SLTRegister.add(SLT, 10, TimeUnit.SECONDS);

        RestAuthorizationResponse authorizationResponse = new RestAuthorizationResponse();
        authorizationResponse.setAuthorizationToken(SLT);

        return Responses.ok(authorizationResponse);
    }
}
