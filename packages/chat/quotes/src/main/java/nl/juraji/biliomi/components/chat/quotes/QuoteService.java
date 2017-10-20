package nl.juraji.biliomi.components.chat.quotes;

import nl.juraji.biliomi.components.system.channel.GameService;
import nl.juraji.biliomi.model.chat.Quote;
import nl.juraji.biliomi.model.chat.QuoteDao;
import nl.juraji.biliomi.model.core.Game;
import nl.juraji.biliomi.model.core.User;
import org.joda.time.DateTime;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
@Default
public class QuoteService {

  @Inject
  private QuoteDao quoteDao;

  @Inject
  private GameService gameService;

  /**
   * Register a new quote
   *
   * @param user The user that made the quote
   * @param message The quote
   * @return The persisted Quote
   */
  public Quote registerQuote(User user, String message) {
    Game currentGame = gameService.getCurrent();

    Quote quote = new Quote();
    quote.setUser(user);
    quote.setMessage(message);
    quote.setDate(DateTime.now());
    quote.setGameAtMoment(currentGame);

    quoteDao.save(quote);

    return quote;
  }

  public Quote getQuote(long id) {
    return quoteDao.get(id);
  }

  public Quote getRandom() {
    return quoteDao.getRandom();
  }

  public void delete(Quote quote) {
    quoteDao.delete(quote);
  }
}
