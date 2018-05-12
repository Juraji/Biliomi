package nl.juraji.biliomi.model.core;

import nl.juraji.biliomi.utility.jpa.JpaDao;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import javax.enterprise.inject.Default;
import java.util.List;

/**
 * Created by Juraji on 13-5-2017.
 * Biliomi v3
 */
@Default
public class TemplateDao extends JpaDao<Template> {

    public TemplateDao() {
        super(Template.class);
    }

    @Override
    public List<Template> getList() {
        return criteria()
                .addOrder(Order.asc("templateKey"))
                .getList();
    }

    public Template getByKey(String key) {
        return criteria()
                .add(Restrictions.eq("templateKey", key))
                .getResult();
    }

    public boolean templateMissing(String key) {
        return criteria()
                .add(Restrictions.eq("templateKey", key))
                .getCount() <= 0;
    }
}
