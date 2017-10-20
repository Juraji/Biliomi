package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.utility.jpa.JpaDao;

/**
 * Created by Juraji on 23-5-2017.
 * Biliomi v3
 */
public class QuoteDao extends JpaDao<Quote> {

  public QuoteDao() {
    super(Quote.class);
  }

  public Quote getRandom() {
    return criteria().getRandom();
  }
}
