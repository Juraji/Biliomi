package nl.juraji.biliomi.io.api.twitch.pubsub.model.commands;

import nl.juraji.biliomi.io.api.twitch.pubsub.model.types.PubSubMessageType;

/**
 * Created by Juraji on 3-9-2017.
 * Biliomi v3
 */
public interface PubSubCommand {
  void setType(PubSubMessageType type);
  PubSubMessageType getType();
}
