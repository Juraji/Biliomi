package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UpdateMode;
import nl.juraji.biliomi.utility.settings.UpdateModeType;
import nl.juraji.biliomi.utility.settings.UserSettings;
import nl.juraji.biliomi.utility.types.Templater;
import org.apache.logging.log4j.Logger;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static nl.juraji.biliomi.utility.types.Templater.template;

/**
 * Created by Juraji on 18-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public final class EntityManagerFactoryProducer {

  @Inject
  private UserSettings userSettings;

  @Inject
  private Logger logger;

  @Inject
  @UpdateMode
  private UpdateModeType updateMode;

  private EntityManagerFactory entityManagerFactory;

  @PostConstruct
  private void initEntityManagerFactoryProducer() {
    boolean useH2Database = userSettings.getBooleanValue("biliomi.database.useH2Database");
    String ddlMode = updateMode.getDdlMode();

    if (useH2Database) {
      try {
        entityManagerFactory = setupH2EMF(ddlMode);
      } catch (IOException e) {
        BiliomiContainer.getContainer().shutdownInError();
      }
    } else {
      entityManagerFactory = setupMySQLEMF(ddlMode);
    }
  }

  @PreDestroy
  private void destroyEntityManagerFactoryProducer() {
    if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
      entityManagerFactory.close();
    }
  }

  @Produces
  public EntityManagerFactory getEntityManagerFactory() {
    return entityManagerFactory;
  }

  private EntityManagerFactory setupH2EMF(String ddlMode) throws IOException {
    Map<String, Object> configuration = new HashMap<>();

    String jdbcUri = Templater.template("jdbc:h2:file:{{datapath}}/datastore")
        .add("datapath", BiliomiContainer.getParameters().getWorkingDir().getCanonicalPath())
        .apply();

    configuration.put("hibernate.connection.url", jdbcUri);
    configuration.put("hibernate.hbm2ddl.auto", ddlMode);

    // Create Entity manager factory
    logger.debug("Creating entity manager factory for local H2 database...");
    HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
    return provider.createEntityManagerFactory("Biliomi-H2-DS", configuration);
  }

  private EntityManagerFactory setupMySQLEMF(String ddlMode) {
    Map<String, Object> configuration = new HashMap<>();
    boolean useSSL = userSettings.getBooleanValue("biliomi.database.usessl");

    // Biliomi doesn't need the MySQL server to be in the correct timezone since all dates
    // are persisted as ISO8601, but a server might stall connection if this isn't set.
    TimeZone timeZone = Calendar.getInstance().getTimeZone();
    configuration.put("hibernate.connection.serverTimezone", timeZone.getID());

    String jdbcUri = template("jdbc:mysql://{{host}}:{{port}}/{{database}}")
        .add("host", userSettings.getValue("biliomi.database.host", "localhost"))
        .add("port", userSettings.getValue("biliomi.database.port", "3306"))
        .add("database", userSettings.getValue("biliomi.database.database", "biliomi"))
        .apply();

    configuration.put("hibernate.connection.url", jdbcUri);
    configuration.put("hibernate.connection.username", userSettings.getValue("biliomi.database.username", "biliomi"));
    configuration.put("hibernate.connection.password", userSettings.getValue("biliomi.database.password", ""));
    configuration.put("hibernate.connection.useSSL", String.valueOf(useSSL));
    configuration.put("hibernate.hbm2ddl.auto", ddlMode);

    // Create Entity manager factory
    logger.debug("Creating entity manager factory for MySQL database...");
    HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
    return provider.createEntityManagerFactory("Biliomi-MySQL-DS", configuration);
  }
}
