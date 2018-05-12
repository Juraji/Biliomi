package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.config.core.YamlCoreSettings;
import nl.juraji.biliomi.config.core.biliomi.UpdateModeType;
import nl.juraji.biliomi.config.core.biliomi.database.USMySQL;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.UpdateMode;
import nl.juraji.biliomi.utility.factories.reflections.ReflectionUtils;
import org.apache.logging.log4j.Logger;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static nl.juraji.biliomi.utility.types.Templater.template;

/**
 * Created by Juraji on 18-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public final class EntityManagerFactoryProducer {

    @Inject
    private YamlCoreSettings yamlCoreSettings;

    @Inject
    private Logger logger;

    @Inject
    @UpdateMode
    private UpdateModeType updateMode;

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    private void initEntityManagerFactoryProducer() {
        boolean useH2Database = yamlCoreSettings.getBiliomi().getDatabase().isUseH2Database();
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

        String jdbcUri = template("jdbc:h2:file:{{datapath}}/datastore")
                .add("datapath", BiliomiContainer.getParameters().getWorkingDir().getCanonicalPath())
                .apply();

        configuration.put("hibernate.connection.url", jdbcUri);
        configuration.put("hibernate.hbm2ddl.auto", ddlMode);

        // Add entity classes
        configuration.put(AvailableSettings.LOADED_CLASSES, scanForEntityClasses());

        // Create Entity manager factory
        logger.debug("Creating entity manager factory for local H2 database...");
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        return provider.createEntityManagerFactory("Biliomi-H2-DS", configuration);
    }

    private EntityManagerFactory setupMySQLEMF(String ddlMode) {
        Map<String, Object> configuration = new HashMap<>();
        USMySQL mySQL = yamlCoreSettings.getBiliomi().getDatabase().getMySQL();
        boolean useSSL = mySQL.isUsessl();

        // Biliomi doesn't need the MySQL server to be in the correct timezone since all dates
        // are persisted as ISO8601, but a server might stall connection if this isn't set.
        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        configuration.put("hibernate.connection.serverTimezone", timeZone.getID());

        String jdbcUri = template("jdbc:mysql://{{host}}:{{port}}/{{database}}")
                .add("host", mySQL::getHost)
                .add("port", mySQL::getPort)
                .add("database", mySQL::getDatabase)
                .apply();

        configuration.put("hibernate.connection.url", jdbcUri);
        configuration.put("hibernate.connection.username", mySQL.getUsername());
        configuration.put("hibernate.connection.password", mySQL.getPassword());
        configuration.put("hibernate.connection.useSSL", String.valueOf(useSSL));
        configuration.put("hibernate.hbm2ddl.auto", ddlMode);

        // Add entity classes
        configuration.put(AvailableSettings.LOADED_CLASSES, scanForEntityClasses());

        // Create Entity manager factory
        logger.debug("Creating entity manager factory for MySQL database...");
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();
        return provider.createEntityManagerFactory("Biliomi-MySQL-DS", configuration);
    }

    private List<Class<?>> scanForEntityClasses() {
        return ReflectionUtils.forPackages("nl.juraji.biliomi.model")
                .annotatedTypes(Entity.class)
                .collect(Collectors.toList());
    }
}
