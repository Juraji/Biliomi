package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import nl.juraji.biliomi.model.core.User;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;

/**
 * Created by Juraji on 21-5-2017.
 * Biliomi v3
 */
@Default
public class UserGreetingDao extends JpaDao<UserGreeting> {
  public UserGreetingDao() {
    super(UserGreeting.class);
  }

  public UserGreeting getByUser(User user) {
    return criteria()
        .createAlias("user", "u")
        .add(Restrictions.eq("u.id", user.getId()))
        .getResult();
  }
}
