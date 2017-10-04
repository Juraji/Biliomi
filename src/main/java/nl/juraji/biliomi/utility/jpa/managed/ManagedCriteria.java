package nl.juraji.biliomi.utility.jpa.managed;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * Created by Juraji on 29-4-2017.
 * Biliomi v3
 */
public class ManagedCriteria<T> extends Managed {
  private static final String RAND_SQL_RESTRICTION = "1=1 order by rand()";

  private final Criteria criteria;

  public ManagedCriteria(EntityManagerFactory emf, Class<T> entityClass) {
    super(emf);
    criteria = session.createCriteria(entityClass);
  }

  public ManagedCriteria<T> add(Criterion criterion) {
    validateSession();
    criteria.add(criterion);
    return this;
  }

  public ManagedCriteria<T> addOrder(Order order) {
    validateSession();
    criteria.addOrder(order);
    return this;
  }

  public ManagedCriteria<T> createAlias(String property, String alias) {
    validateSession();
    criteria.createAlias(property, alias);
    return this;
  }

  public <R> ManagedCriteria<R> setProjection(Projection projection, Class<R> targetType) {
    if (targetType == null) {
      throw new IllegalArgumentException("targetType cannot be NULL");
    }

    validateSession();
    criteria.setProjection(projection);
    //noinspection unchecked
    return (ManagedCriteria<R>) this;
  }

  public ManagedCriteria<T> setResultTransformer(ResultTransformer resultTransformer) {
    validateSession();
    criteria.setResultTransformer(resultTransformer);
    return this;
  }

  public T getRandom() {
    criteria.add(Restrictions.sqlRestriction(RAND_SQL_RESTRICTION));
    return getResult();
  }

  @SuppressWarnings("unchecked")
  public T getResult() {
    T result;

    try {
      validateSession();
      result = (T) criteria
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
          .uniqueResult();
    } finally {
      session.close();
    }

    return result;
  }

  public List<T> getList() {
    return getList(-1);
  }

  @SuppressWarnings("unchecked")
  public List<T> getList(int maxResults) {
    List<T> list;

    try {
      validateSession();
      this.criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

      if (maxResults > -1) {
        this.criteria.setMaxResults(maxResults);
      }

      list = criteria.list();
    } finally {
      session.close();
    }

    return list;
  }

  public long getCount() {
    long count;

    try {
      validateSession();
      count = (long) criteria
          .setProjection(Projections.rowCount())
          .uniqueResult();
    } finally {
      session.close();
    }

    return count;
  }
}
