package nl.juraji.biliomi;

import nl.juraji.biliomi.utility.types.AppParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;
import org.jboss.weld.environment.se.Weld;

import javax.enterprise.inject.spi.CDI;

/**
 * Created by Juraji on 25-4-2017.
 * Biliomi v3
 */
public final class BiliomiContainer {
  private static BiliomiContainer container;
  private static AppParameters appParameters;
  private Weld weld;

  static {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    Log.setLog(new JavaUtilLog());
  }

  private BiliomiContainer() {
  }

  public static void main(String[] args) throws Exception {
    appParameters = new AppParameters(args);
    container = new BiliomiContainer();
    container.go();
    registerShutdownHooks();
  }

  public static AppParameters getParameters() {
    return appParameters;
  }

  public static BiliomiContainer getContainer() {
    return container;
  }


  // Preferably use this method to programmatically exit Biliomi
  public void shutdownInError() {
    shutdownNow(1);
  }

  // Preferably use this method to programmatically exit Biliomi
  public void shutdownNow(int status) {
    System.exit(status);
  }

  public void restartNow() {
    this.weld.shutdown();
    go();
  }

  private static void registerShutdownHooks() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (LogManager.getContext() instanceof LoggerContext) {
        Configurator.shutdown((LoggerContext) LogManager.getContext());
      }
    }));
  }

  private void go() {
    this.weld = new Weld("Biliomi");
    this.weld.initialize();
    Biliomi biliomi = CDI.current().select(Biliomi.class).get();
    biliomi.run();
  }
}
