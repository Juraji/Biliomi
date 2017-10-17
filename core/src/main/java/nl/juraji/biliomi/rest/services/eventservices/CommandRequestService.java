package nl.juraji.biliomi.rest.services.eventservices;

import nl.juraji.biliomi.model.internal.events.bot.ConsoleInputEvent;
import nl.juraji.biliomi.model.internal.events.irc.user.messages.IrcChatMessageEvent;
import nl.juraji.biliomi.model.internal.rest.CommandRequest;
import nl.juraji.biliomi.rest.config.Responses;
import nl.juraji.biliomi.rest.config.RestRequestInfoHolder;
import nl.juraji.biliomi.utility.commandrouters.routers.CliCommandRouter;
import nl.juraji.biliomi.utility.commandrouters.routers.CommandRouter;
import nl.juraji.biliomi.utility.types.MutableString;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/events/commands")
public class CommandRequestService {

  @Inject
  private CommandRouter commandRouter;

  @Inject
  private CliCommandRouter cliCommandRouter;

  @POST
  @Path("/run")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response runBotCommand(CommandRequest commandRequest) {
    if (StringUtils.isEmpty(commandRequest.getCommand())) {
      return Responses.badRequest();
    }

    MutableString command = new MutableString(commandRequest.getCommand());
    command.prependIfMissing("!");
    String username = RestRequestInfoHolder.getRequestInfo().getUsername();
    IrcChatMessageEvent event = new IrcChatMessageEvent(username, null, command.toString());
    boolean success = commandRouter.runCommand(event, true);

    if (success) {
      return Responses.ok();
    } else {
      return Responses.notModified();
    }
  }

  @POST
  @Path("/cli/run")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response runCliCommand(CommandRequest commandRequest) {
    if (StringUtils.isEmpty(commandRequest.getCommand())) {
      return Responses.badRequest();
    }

    MutableString command = new MutableString(commandRequest.getCommand());
    command.prependIfMissing("/");
    ConsoleInputEvent event = new ConsoleInputEvent(command.toString(), false);
    cliCommandRouter.onConsoleInputEvent(event);

    return Responses.ok();
  }
}
