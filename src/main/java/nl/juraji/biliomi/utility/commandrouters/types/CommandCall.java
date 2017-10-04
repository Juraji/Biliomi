package nl.juraji.biliomi.utility.commandrouters.types;

import com.google.common.base.Splitter;

import java.util.List;

/**
 * Created by Juraji on 2-5-2017.
 * Biliomi v3
 */
public final class CommandCall {
  private final String command;
  private final List<String> argList;

  public CommandCall(String message) {
    List<String> parts = Splitter.on(" ").splitToList(message.trim());
    this.command = parts.get(0).substring(1);
    this.argList = parts.subList(1, parts.size());
  }

  public String getCommand() {
    return command;
  }

  public Arguments getArguments() {
    return new Arguments(command, argList);
  }

  public static boolean isCallable(String message) {
    return (message.length() > 1 && message.startsWith("!") && !message.startsWith("!!"));
  }
}
