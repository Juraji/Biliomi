package nl.juraji.biliomi.utility.types.collections;

import nl.juraji.biliomi.utility.types.Templater;
import nl.juraji.biliomi.utility.types.enums.OnOff;
import nl.juraji.biliomi.utility.types.enums.StreamState;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Juraji on 17-4-2017.
 * Biliomi v3
 */
public interface L10nMap {
  Templater get(String key);
  String getString(String key);
  Supplier<String> supply(String key);
  List<String> getKeyStartsWith(String keyPrefix);
  String getIfElse(boolean state, String trueKey, String falseKey);
  String getEnabledDisabled(OnOff state);
  String getEnabledDisabled(boolean state);
  String getAllowedDisallowed(boolean state);
  String getStreamState(StreamState streamState);
  String getUserNonExistent(String username);
  String getCommandNonExistent(String command);
  String getGroupNonExistent(String groupName);
  String getInputContainsBadWords();
  String getGenericError(String user, String command, String errorMessage);
}
