package nl.juraji.biliomi.model.registers;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import nl.juraji.biliomi.model.core.Direction;
import nl.juraji.biliomi.model.core.User;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
public class RaidRecordDao extends JpaDao<RaidRecord> {

  public RaidRecordDao() {
    super(RaidRecord.class);
  }

  public long getCount(User channel, Direction direction) {
    return criteria()
        .createAlias("channel", "c")
        .add(Restrictions.and(
            Restrictions.eq("c.id", channel.getId()),
            Restrictions.eq("direction", direction)
        ))
        .addOrder(Order.desc("id"))
        .getCount();
  }

  public List<RaidRecord> getListByDirection(Direction direction) {
    return criteria()
        .add(Restrictions.eq("direction", direction))
        .addOrder(Order.desc("id"))
        .getList();
  }
}
