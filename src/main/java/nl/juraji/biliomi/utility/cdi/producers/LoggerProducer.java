package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Created by Juraji on 4-4-2017.
 * biliomi
 */
public final class LoggerProducer {

  static {
    if (BiliomiContainer.getParameters().isDebugMode()) {
      Configurator.setLevel("nl.juraji.biliomi", Level.DEBUG);
    }
  }

  @Produces
  public Logger getLogger(InjectionPoint injectionPoint) {
    return LogManager.getLogger(injectionPoint.getBean().getBeanClass());
  }
}
