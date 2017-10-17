package nl.juraji.biliomi.utility.jpa;

import nl.juraji.biliomi.utility.jpa.managed.ManagedCriteria;
import nl.juraji.biliomi.utility.jpa.managed.ManagedTransaction;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Restrictions;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Juraji on 2-4-2017.
 * biliomi
 */
@SuppressWarnings({"CdiManagedBeanInconsistencyInspection"})
public abstract class JpaDao<T> {

  @Inject
  private EntityManagerFactory emf;

  @Inject
  private Logger logger;

  private final Class<T> entityClass;

  public JpaDao(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Get a ManagedCriteria for entity lookup
   *
   * @return A new ManagedCriteria
   */
  protected ManagedCriteria<T> criteria() {
    return new ManagedCriteria<>(emf, entityClass);
  }

  /**
   * Do work within a transaction
   *
   * @return A new ManagedTransaction
   */
  protected ManagedTransaction safeTransaction() {
    return new ManagedTransaction(emf);
  }

  /**
   * Convenience method
   * Get a single entity by id
   *
   * @param id The entity id to look up
   * @return The found entity else null
   */
  public T get(long id) {
    return criteria().add(Restrictions.eq("id", id)).getResult();
  }

  /**
   * Convenience method
   * Get a list of entities
   *
   * @return A list of entities
   */
  public List<T> getList() {
    return criteria().getList();
  }

  /**
   * Persist an entity
   *
   * @param entity The entity to persist
   */
  public void save(T entity) {
    if (entity != null) {
      new ManagedTransaction(emf)
          .withExceptionConsumer(e -> logger.error("Could not save entity", e))
          .executeWithinTransaction(session -> session.saveOrUpdate(entity));
    }
  }

  /**
   * Persist a collection of entities
   *
   * @param entities The collection of entities to persist
   */
  public void save(Collection<T> entities) {
    if (entities != null && !entities.isEmpty()) {
      new ManagedTransaction(emf)
          .withExceptionConsumer(e -> logger.error("Could not save entities", e))
          .executeWithinTransaction(session -> entities.stream()
              .filter(Objects::nonNull)
              .forEach(session::saveOrUpdate));
    }
  }

  /**
   * Delete an entity
   *
   * @param entity The entity to delete
   */
  public void delete(T entity) {
    if (entity != null) {
      new ManagedTransaction(emf)
          .withExceptionConsumer(e -> logger.error("Could not delete entity", e))
          .executeWithinTransaction(session -> {
            if (session.contains(entity)) {
              session.delete(entity);
            } else {
              session.delete(session.merge(entity));
            }
          });
    }
  }

  /**
   * Delete a list of entities
   *
   * @param entities The collection of entities to delete
   */
  public void delete(Collection<T> entities) {
    if (entities != null && !entities.isEmpty()) {
      new ManagedTransaction(emf)
          .withExceptionConsumer(e -> logger.error("Could not delete entities", e))
          .executeWithinTransaction(session -> entities.stream()
              .filter(Objects::nonNull)
              .map(entity -> (session.contains(entity) ? entity : session.merge(entity)))
              .forEach(session::delete));
    }
  }
}
