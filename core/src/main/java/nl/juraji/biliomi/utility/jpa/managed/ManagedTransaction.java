package nl.juraji.biliomi.utility.jpa.managed;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;

/**
 * Created by Juraji on 29-4-2017.
 * Biliomi v3
 */
public class ManagedTransaction extends Managed {
    private Consumer<Exception> exceptionConsumer;

    public ManagedTransaction(EntityManagerFactory emf) {
        super(emf);
    }

    public ManagedTransaction withExceptionConsumer(Consumer<Exception> exceptionConsumer) {
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    public void executeWithinTransaction(Consumer<Session> sessionConsumer) {
        validateSession();
        Transaction transaction = session.beginTransaction();
        try {
            sessionConsumer.accept(session);
            transaction.commit();
            session.flush();
        } catch (Exception e) {
            transaction.rollback();
            exceptionConsumer.accept(e);
            throw e;
        } finally {
            session.close();
        }
    }
}
