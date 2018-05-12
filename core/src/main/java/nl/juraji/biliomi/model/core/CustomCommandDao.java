package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;

/**
 * Created by Juraji on 4-5-2017.
 * Biliomi v3
 */
@Default
public class CustomCommandDao extends JpaDao<CustomCommand> {
    public CustomCommandDao() {
        super(CustomCommand.class);
    }

    public CustomCommand getCommand(String command) {
        return criteria()
                .add(Restrictions.eq("command", command))
                .getResult();
    }

    public CustomCommand getByAlias(String alias) {
        return criteria()
                .createAlias("aliasses", "a")
                .add(Restrictions.eq("a.elements", alias))
                .getResult();
    }
}
