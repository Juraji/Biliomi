package nl.juraji.biliomi.components.integrations.panelserver;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;
import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.config.panelserver.PanelServerConfigService;
import nl.juraji.biliomi.utility.types.Restartable;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by Juraji on 30-1-2018.
 * Biliomi
 */
@Default
@Singleton
public class PanelServerService implements Restartable {

  @Inject
  private Logger logger;

  @Inject
  private PanelServerConfigService configService;

  private SimpleWebServer webServer;

  @Override
  public void start() {
    if (this.webServer == null && configService.isEnablePanelServer()) {
      File panelRoot = getServerRoot();
      String serverHost = configService.getServerHost();
      int serverPort = configService.getServerPort();
      String allowedOrigin = configService.getCorsAllowedOrigin();

      this.webServer = new SimpleWebServer(serverHost, serverPort, panelRoot, true, allowedOrigin) {
        @Override
        protected Response getNotFoundResponse() {
          // Respond with index.html when an item is not found
          File index = new File(getServerRoot(), "index.html");
          try {
            String content = FileUtils.readFileToString(index, "UTF-8");
            return newFixedLengthResponse(Response.Status.OK, MIME_HTML, content);
          } catch (IOException e) {
            logger.error("Failed index.html fallback", e);
          }

          return getInternalErrorResponse("Internal error");
        }
      };

      try {
        this.webServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
      } catch (IOException e) {
        logger.error("Failed starting webserver", e);
      }

      logger.info("Panel webserver started at http://" + serverHost + ":" + serverPort);
    } else {
      logger.info("Panel webserver already running or disabled by config");
    }
  }

  @Override
  public void stop() {
    if (this.webServer != null) {
      this.webServer.stop();
      this.webServer = null;
      logger.info("Panel webserver stopped");
    }
  }

  public File getServerRoot() {
    String serverRootPath = configService.getServerRoot();

    if (serverRootPath == null) {
      // Use the default root if the root in the config is null
      serverRootPath = BiliomiContainer.getParameters().getRootDir() + "/panel";
    }

    return new File(serverRootPath);
  }

  public boolean isPanelSourcePresent() {
    boolean indexFound = false;

    try {
      Collection<File> files = FileUtils.listFiles(getServerRoot(), new String[]{"html"}, false);
      indexFound = !files.isEmpty();
    } catch (IllegalArgumentException e) {
      logger.error("Failed checking panel sources", e);
    }

    return indexFound;
  }
}
