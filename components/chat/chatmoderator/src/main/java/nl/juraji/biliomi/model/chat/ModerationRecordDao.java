package nl.juraji.biliomi.model.chat;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class ModerationRecordDao extends JpaDao<ModerationRecord> {

  public ModerationRecordDao() {
    super(ModerationRecord.class);
  }

  public List<ModerationRecord> getRecords(Long userId) {
    User user = new User();
    user.setId(userId);
    return getRecords(user);
  }

  public List<ModerationRecord> getRecords(User user) {
    return criteria()
        .createAlias("user", "u")
        .add(Restrictions.eq("u.id", user.getId()))
        .getList();
  }
}
