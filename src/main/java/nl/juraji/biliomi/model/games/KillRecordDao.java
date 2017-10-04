package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 22-5-2017.
 * Biliomi v3
 */
@Default
public class KillRecordDao extends JpaDao<KillRecord> {

  public KillRecordDao() {
    super(KillRecord.class);
  }

  public List<KillRecord> getRecords(User user) {
    return getRecords(user, -1);
  }

  public List<KillRecord> getRecords(User user, int limit) {
    return criteria()
        .createAlias("killer", "k")
        .createAlias("target", "t")
        .add(Restrictions.or(
            Restrictions.eq("k.id", user.getId()),
            Restrictions.eq("t.id", user.getId())
        ))
        .addOrder(Order.desc("id"))
        .getList(limit);
  }
}
