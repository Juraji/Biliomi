package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Default
public class UserGroupDao extends JpaDao<UserGroup> {

  public UserGroupDao() {
    super(UserGroup.class);
  }

  @Override
  public List<UserGroup> getList() {
    return criteria()
        .addOrder(Order.asc("weight"))
        .getList();
  }

  public UserGroup getByName(String name) {
    return criteria()
        .add(Restrictions.eq("name", name).ignoreCase())
        .getResult();
  }

  public boolean groupExists(String name) {
    return criteria()
        .add(Restrictions.eq("name", name).ignoreCase())
        .getCount() > 0;
  }

  public boolean groupExists(int weight) {
    return criteria()
        .add(Restrictions.eq("weight", weight))
        .getCount() > 0;
  }

  public UserGroup getDefaultGroup() {
    return criteria()
        .add(Restrictions.eq("defaultGroup", true))
        .getResult();
  }

  public List<UserGroup> getTimeBasedGroups() {
    return criteria()
        .add(Restrictions.isNotNull("levelUpHours"))
        .getList();
  }
}
