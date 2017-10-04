package nl.juraji.biliomi.model.registers;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import nl.juraji.biliomi.model.core.Direction;
import nl.juraji.biliomi.model.core.User;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 17-5-2017.
 * Biliomi v3
 */
@Default
public class HostRecordDao extends JpaDao<HostRecord> {

  public HostRecordDao() {
    super(HostRecord.class);
  }

  public HostRecord getLatestRecord(User channel, Direction direction) {
    return criteria()
        .createAlias("user", "u")
        .add(Restrictions.and(
            Restrictions.eq("u.id", channel.getId()),
            Restrictions.eq("direction", direction)
        ))
        .addOrder(Order.desc("id"))
        .getResult();
  }

  public List<HostRecord> getListByDirection(Direction direction) {
    return criteria()
        .add(Restrictions.eq("direction", direction))
        .addOrder(Order.desc("id"))
        .getList();
  }
}
