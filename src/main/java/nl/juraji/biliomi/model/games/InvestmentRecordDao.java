package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 27-5-2017.
 * Biliomi v3
 */
@Default
public class InvestmentRecordDao extends JpaDao<InvestmentRecord> {

  public InvestmentRecordDao() {
    super(InvestmentRecord.class);
  }

  public List<InvestmentRecord> getRecords(User user) {
    return getRecords(user, -1);
  }

  public List<InvestmentRecord> getRecords(User user, int limit) {
    return criteria()
        .createAlias("invester", "i")
        .add(Restrictions.eq("i.id", user.getId()))
        .addOrder(Order.desc("id"))
        .getList(limit);
  }
}
