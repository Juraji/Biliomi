package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.listeners;

import io.socket.emitter.Emitter;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model.SocketEvent;
import nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model.message.DonationMessage;
import nl.juraji.biliomi.model.internal.events.StreamLabsDonationEvent;
import nl.juraji.biliomi.utility.events.EventBus;

import java.io.IOException;

import static nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller.convertJsonNode;
import static nl.juraji.biliomi.utility.factories.marshalling.JacksonMarshaller.unmarshal;

/**
 * Created by Juraji on 3-10-2017.
 * Biliomi
 */
public class StreamLabsEventListener implements Emitter.Listener {

  private final EventBus eventBus;

  public StreamLabsEventListener(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void call(Object... objects) {
    try {
      String eventData = String.valueOf(objects[0]);
      SocketEvent event = unmarshal(eventData, SocketEvent.class);

      if (event.getType() == null) {
        // Event type was not recognized, ignore message
        return;
      }

      switch (event.getType()) {
        case DONATION:
          event.getMessage().stream()
              .map(node -> convertJsonNode(node, DonationMessage.class))
              .forEach(this::handleDonationMessage);
          break;
        default:
          break;
      }
    } catch (IOException ignored) {
      // Ignored, if marshalling fails it was likely not a message which Biliomi understands
    }
  }

  private void handleDonationMessage(DonationMessage donationMessage) {
    StreamLabsDonationEvent event = new StreamLabsDonationEvent(
        donationMessage.getName(),
        donationMessage.getFormattedAmount(),
        donationMessage.getAmount(),
        donationMessage.getMessage()
    );

    eventBus.post(event);
  }
}
