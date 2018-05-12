package nl.juraji.biliomi.rest.services.chat;

import nl.juraji.biliomi.model.chat.Quote;
import nl.juraji.biliomi.model.chat.QuoteDao;
import nl.juraji.biliomi.rest.config.ModelRestService;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by Juraji on 18-6-2017.
 * Biliomi v3
 */
@Path("/chat/quotes")
public class QuoteRestService extends ModelRestService<Quote> {

    @Inject
    private QuoteDao quoteDao;

    @Override
    public List<Quote> getEntities() {
        return quoteDao.getList();
    }

    @Override
    public Quote getEntity(long id) {
        return quoteDao.get(id);
    }

    @Override
    public Quote createEntity(Quote e) {
        quoteDao.save(e);
        return e;
    }

    @Override
    public Quote updateEntity(Quote e, long id) {
        Quote quote = quoteDao.get(id);

        quote.setUser(e.getUser());
        quote.setMessage(e.getMessage());
        quote.setDate(e.getDate());
        quote.setGameAtMoment(e.getGameAtMoment());

        quoteDao.save(quote);
        return quote;
    }

    @Override
    public boolean deleteEntity(long id) {
        Quote quote = quoteDao.get(id);

        if (quote == null) {
            return false;
        }

        quoteDao.delete(quote);
        return true;
    }
}
