package nl.juraji.biliomi.model.registers;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Juraji on 24-5-2017.
 * Biliomi v3
 */
public class DonationDao extends JpaDao<Donation> {

    public DonationDao() {
        super(Donation.class);
    }

    @Override
    public List<Donation> getList() {
        return criteria()
                .addOrder(Order.desc("date"))
                .getList();
    }

    public List<Donation> getDonationsFor(User user) {
        return criteria()
                .createAlias("user", "u")
                .add(Restrictions.eq("u.id", user.getId()))
                .getList();
    }

    public long getCount(User user) {
        return criteria()
                .createAlias("user", "u")
                .add(Restrictions.eq("u.id", user.getId()))
                .getCount();
    }
}
