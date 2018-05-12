package nl.juraji.biliomi.utility.jpa.managed;

import org.hibernate.Session;
import org.hibernate.jpa.HibernateEntityManager;

import javax.persistence.EntityManagerFactory;

/**
 * Created by Juraji on 30-4-2017.
 * Biliomi v3
 */
public abstract class Managed {
    protected final Session session;

    public Managed(EntityManagerFactory emf) {
        session = emf.createEntityManager().unwrap(HibernateEntityManager.class).getSession();
    }

    protected void validateSession() {
        if (!session.isOpen()) {
            throw new IllegalStateException(getClass().getSimpleName() + " cannot be reused");
        }
    }
}
