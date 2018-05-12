package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import nl.juraji.biliomi.utility.jpa.managed.ManagedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Default
public class UserDao extends JpaDao<User> {

    public UserDao() {
        super(User.class);
    }

    @Override
    public List<User> getList() {
        return criteria()
                .addOrder(Order.asc("username"))
                .getList();
    }

    public User getByUsername(String username) {
        return criteria()
                .add(Restrictions.eq("username", username).ignoreCase())
                .getResult();
    }

    public User getByTwitchId(long twitchId) {
        return criteria()
                .add(Restrictions.eq("twitchUserId", twitchId))
                .getResult();
    }

    public List<User> getUsersByGroup(UserGroup userGroup) {
        return criteria()
                .createAlias("userGroup", "g")
                .add(Restrictions.eq("g.id", userGroup.getId()))
                .getList();
    }

    public User getCaster() {
        return criteria()
                .add(Restrictions.eq("caster", true))
                .getResult();
    }

    public List<User> getModerators() {
        return criteria()
                .add(Restrictions.or(
                        Restrictions.eq("caster", true),
                        Restrictions.eq("moderator", true)
                ))
                .getList();
    }

    public List<User> getFollowers() {
        return getFollowers(-1);
    }

    public List<User> getFollowers(int limit) {
        return criteria()
                .add(Restrictions.eq("follower", true))
                .addOrder(Order.desc("followDate"))
                .setMaxResults(limit)
                .getList();
    }

    public long getFollowerCount() {
        return criteria()
                .add(Restrictions.eq("follower", true))
                .getCount();
    }

    public List<User> getSubscribers() {
        return getSubscribers(-1);
    }

    public List<User> getSubscribers(int limit) {
        return criteria()
                .add(Restrictions.eq("subscriber", true))
                .addOrder(Order.desc("subscribeDate"))
                .setMaxResults(limit)
                .getList();
    }

    public long getSubscriberCount() {
        return criteria()
                .add(Restrictions.eq("subscriber", true))
                .getCount();
    }

    public List<User> getTopUsersByField(String fieldName, int limit, String[] excludeUsernames) {
        ManagedCriteria<User> criteria = criteria();

        if (excludeUsernames.length > 0) {
            criteria.add(Restrictions.not(Restrictions.in("username", excludeUsernames)));
        }

        return criteria
                .addOrder(Order.desc(fieldName))
                .setMaxResults(limit)
                .getList();
    }
}
