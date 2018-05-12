package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Default
public class CommandDao extends JpaDao<Command> {

    public CommandDao() {
        super(Command.class);
    }

    public Command getCommand(String command) {
        return criteria()
                .add(Restrictions.eq("command", command).ignoreCase())
                .getResult();
    }

    public Command getByAlias(String alias) {
        return criteria()
                .createAlias("aliasses", "a")
                .add(Restrictions.eq("a.elements", alias).ignoreCase())
                .getResult();
    }
}
