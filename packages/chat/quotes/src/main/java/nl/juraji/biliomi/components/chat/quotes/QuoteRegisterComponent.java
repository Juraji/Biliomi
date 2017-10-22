package nl.juraji.biliomi.components.chat.quotes;

import nl.juraji.biliomi.components.shared.TimeFormatter;
import nl.juraji.biliomi.model.chat.Quote;
import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.calculate.Numbers;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.NormalComponent;
import nl.juraji.biliomi.utility.commandrouters.annotations.CommandRoute;
import nl.juraji.biliomi.utility.commandrouters.annotations.SubCommandRoute;
import nl.juraji.biliomi.utility.commandrouters.types.Arguments;
import nl.juraji.biliomi.utility.types.components.Component;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
@Default
@Singleton
@NormalComponent
public class QuoteRegisterComponent extends Component {

  @Inject
  private QuoteService quoteService;

  @Inject
  private TimeFormatter timeFormatter;

  /**
   * Get a quote from the quote register
   * Usage: !quote or !quote [quote id]
   */
  @CommandRoute(command = "quote")
  public boolean quoteCommand(User user, Arguments arguments) {
    Quote quote;

    if (arguments.assertSize(1)) {
      // If quoteId is present in args, try and retrieve it that way
      Long quoteId = Numbers.asNumber(arguments.get(0)).toLong();

      if (quoteId == null) {
        chat.whisper(user, i18n.get("ChatCommand.quote.usage"));
        return false;
      }

      quote = quoteService.getQuote(quoteId);
    } else {
      // No quoteId is given, try and retrieve a random quote
      quote = quoteService.getRandom();
    }

    if (quote == null) {
      chat.whisper(user, i18n.get("ChatCommand.quote.notFound"));
      return false;
    }

    chat.say(i18n.get("ChatCommand.quote.quoteTemplate")
        .add("message", quote::getMessage)
        .add("date", () -> timeFormatter.fullDate(quote.getDate()))
        .add("user", quote.getUser().getNameAndTitle()));
    return true;
  }

  /**
   * Manage quotes
   * Usage: !quotes [add|remove] [more...]
   */
  @CommandRoute(command = "quotes", systemCommand = true)
  public boolean managequotesCommand(User user, Arguments arguments) {
    return captureSubCommands("quotes", i18n.supply("ChatCommand.quotes.usage"), user, arguments);
  }

  /**
   * Add a new quote
   * Usage: !quotes add [username] [message...]
   */
  @SubCommandRoute(parentCommand = "quotes", command = "add")
  public boolean managequotesCommandAdd(User user, Arguments arguments) {
    if (!arguments.assertMinSize(2)) {
      chat.whisper(user, i18n.get("ChatCommand.quotes.add.usage"));
      return false;
    }

    String qUsername = arguments.pop();
    User qUser = usersService.getUser(qUsername, true);
    if (qUser == null) {
      chat.whisper(user, i18n.getUserNonExistent(qUsername));
      return false;
    }

    String message = arguments.toString();

    Quote quote = quoteService.registerQuote(qUser, message);

    chat.say(i18n.get("ChatCommand.quotes.add.saved")
        .add("username", user::getNameAndTitle)
        .add("id", quote::getId));
    return true;
  }

  /**
   * Remove a quote
   * Usage: !quotes remove [quote id]
   */
  @SubCommandRoute(parentCommand = "quotes", command = "remove")
  public boolean managequotesCommandRemove(User user, Arguments arguments) {
    Long quoteId = Numbers.asNumber(arguments.get(0)).toLong();

    if (quoteId == null) {
      chat.whisper(user, i18n.get("ChatCommand.quotes.remove.usage"));
      return false;
    }

    Quote quote = quoteService.getQuote(quoteId);
    if (quote == null) {
      chat.whisper(user, i18n.get("ChatCommand.quote.notFound"));
      return false;
    }

    quoteService.delete(quote);
    chat.whisper(user, i18n.get("ChatCommand.quotes.remove.removed")
        .add("username", quote.getUser().getDisplayName())
        .add("id", quote::getId));
    return true;
  }
}
