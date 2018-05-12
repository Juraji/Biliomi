package nl.juraji.biliomi.model.core.settings;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;
import java.util.Collection;

/**
 * Created by Juraji on 10-4-2017.
 * biliomi
 */
@Default
@Singleton
public class SettingsDao extends JpaDao<Settings> {
    public SettingsDao() {
        super(Settings.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Settings> T getSettings(Class<T> type) {
        return (T) criteria().add(Restrictions.eq("id", type.getSimpleName())).getResult();
    }

    @Override
    public void delete(Settings object) {
        throw new UnsupportedOperationException("Settings can not be deleted");
    }

    @Override
    public void delete(Collection<Settings> objects) {
        throw new UnsupportedOperationException("Settings can not be deleted");
    }
}
