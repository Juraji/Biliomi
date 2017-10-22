package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 5-6-2017.
 * Biliomi v3
 */
@Default
public class AdventureRecordDao extends JpaDao<AdventureRecord> {

  public AdventureRecordDao() {
    super(AdventureRecord.class);
  }

  public List<AdventureRecord> getRecords(User user) {
    return getRecords(user, -1);
  }

  public List<AdventureRecord> getRecords(User user, int limit) {
    return criteria()
        .createAlias("adventurer", "a")
        .add(Restrictions.eq("a.id", user.getId()))
        .addOrder(Order.desc("id"))
        .setMaxResults(limit)
        .getList();
  }

  public long getRecordCount(User user) {
    return criteria()
        .createAlias("adventurer", "a")
        .add(Restrictions.eq("a.id", user.getId()))
        .getCount();
  }
}
