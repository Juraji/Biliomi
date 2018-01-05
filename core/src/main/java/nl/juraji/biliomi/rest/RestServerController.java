package nl.juraji.biliomi.rest;

import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import nl.juraji.biliomi.utility.types.Restartable;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

/**
 * Created by Juraji on 13-6-2017.
 * Biliomi v3
 */
@Default
@Singleton
public class RestServerController implements Restartable {
  private static final String SERVICES_PACKAGE = "nl.juraji.biliomi.rest.services";
  private static final String PROVIDERS_PACKAGE = "nl.juraji.biliomi.rest.config.providers";

  @Inject
  private Logger logger;

  @Inject
  @AppData("rest.api.uris.base")
  private String apiBaseUri;

  private HttpServer server;

  @Override
  public void start() {
    if (server == null) {
      final ResourceConfig resourceConfig = new ResourceConfig()
          .packages(true, SERVICES_PACKAGE, PROVIDERS_PACKAGE);

      server = GrizzlyHttpServerFactory.createHttpServer(URI.create(apiBaseUri), resourceConfig);
      logger.info("Started the HTTP REST server on {}", apiBaseUri);
    }
  }

  @PreDestroy
  @Override
  public void stop() {
    if (server != null && server.isStarted()) {
      server.shutdownNow();
      server = null;
    }
  }
}
