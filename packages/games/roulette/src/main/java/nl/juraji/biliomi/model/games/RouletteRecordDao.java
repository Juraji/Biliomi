package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
@Default
public class RouletteRecordDao extends JpaDao<RouletteRecord> {

    public RouletteRecordDao() {
        super(RouletteRecord.class);
    }

    public List<RouletteRecord> getRecords(User user) {
        return getRecords(user, -1);
    }

    public List<RouletteRecord> getRecords(User user, int limit) {
        return criteria()
                .createAlias("user", "u")
                .add(Restrictions.eq("u.id", user.getId()))
                .addOrder(Order.desc("id"))
                .setMaxResults(limit)
                .getList();
    }
}
