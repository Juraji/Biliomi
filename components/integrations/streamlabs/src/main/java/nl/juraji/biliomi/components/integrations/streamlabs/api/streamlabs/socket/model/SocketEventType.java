package nl.juraji.biliomi.components.integrations.streamlabs.api.streamlabs.socket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import nl.juraji.biliomi.utility.calculate.EnumUtils;

/**
 * Created by Juraji on 2-10-2017.
 * Biliomi
 */
public enum SocketEventType {
  DONATION, HOST;

  @JsonCreator
  public static SocketEventType fromString(String type) {
    return EnumUtils.toEnum(type, SocketEventType.class);
  }
}
