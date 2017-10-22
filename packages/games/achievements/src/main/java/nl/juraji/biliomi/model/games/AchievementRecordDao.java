package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 22-10-2017.
 * Biliomi
 */
@Default
public class AchievementRecordDao extends JpaDao<AchievementRecord> {

  public AchievementRecordDao() {
    super(AchievementRecord.class);
  }

  public List<AchievementRecord> getRecords(User user) {
    return getRecords(user, -1);
  }

  public List<AchievementRecord> getRecords(User user, int limit) {
    return criteria()
        .createAlias("user", "u")
        .add(Restrictions.eq("u.id", user.getId()))
        .addOrder(Order.desc("id"))
        .setMaxResults(limit)
        .getList();
  }

  public boolean recordExists(User user, String achievementId) {
    return criteria()
        .createAlias("user", "u")
        .add(Restrictions.eq("u.id", user.getId()))
        .add(Restrictions.eq("achievementId", achievementId))
        .getCount() > 0;
  }
}
