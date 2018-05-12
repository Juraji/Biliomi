package nl.juraji.biliomi.model.games;

import nl.juraji.biliomi.model.core.User;
import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 28-5-2017.
 * Biliomi v3
 */
@Default
public class TamagotchiDao extends JpaDao<Tamagotchi> {

    public TamagotchiDao() {
        super(Tamagotchi.class);
    }

    public boolean userHasTamagotchi(User user) {
        return criteria()
                .createAlias("owner", "o")
                .add(Restrictions.and(
                        Restrictions.eq("o.id", user.getId()),
                        Restrictions.eq("deceased", false)
                ))
                .getCount() > 0;
    }

    public Tamagotchi getByUser(User user) {
        return criteria()
                .createAlias("owner", "o")
                .add(Restrictions.and(
                        Restrictions.eq("o.id", user.getId()),
                        Restrictions.eq("deceased", false)
                ))
                .getResult();
    }

    public List<Tamagotchi> getAliveTamagotchis() {
        return criteria()
                .add(Restrictions.eq("deceased", false))
                .getList();
    }

    public List<Tamagotchi> getDeceasedTamagotchis() {
        return criteria()
                .add(Restrictions.eq("deceased", true))
                .getList();
    }
}
