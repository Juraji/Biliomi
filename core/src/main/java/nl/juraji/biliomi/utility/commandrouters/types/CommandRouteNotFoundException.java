package nl.juraji.biliomi.utility.commandrouters.types;

/**
 * Created by Juraji on 7-5-2017.
 * Biliomi v3
 */
public class CommandRouteNotFoundException extends RuntimeException {
  public CommandRouteNotFoundException(String message) {
    super(message);
  }
}
