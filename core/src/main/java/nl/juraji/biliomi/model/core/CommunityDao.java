package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;

/**
 * Created by Juraji on 15-12-2017.
 * Biliomi
 */
@Default
public class CommunityDao extends JpaDao<Community> {

  public CommunityDao() {
    super(Community.class);
  }

  public Community getByName(String name) {
    return criteria()
        .add(Restrictions.eq("name", name).ignoreCase())
        .getResult();
  }

  public Community getByTwitchID(String name) {
    return criteria()
        .add(Restrictions.eq("twitchId", name))
        .getResult();
  }
}
