package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.BiliomiContainer;
import nl.juraji.biliomi.utility.cdi.annotations.modifiers.L10nData;
import nl.juraji.biliomi.utility.types.collections.L10nMap;
import nl.juraji.biliomi.utility.types.collections.L10nMapImpl;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Juraji on 17-4-2017.
 * Biliomi v3
 */
@Default
@Singleton
public final class LocalizationProducer {
  private final File langBase;

  @Inject
  private Logger logger;

  private Properties commonLang;

  public LocalizationProducer() {
    langBase = BiliomiContainer.getParameters().getLanguageBaseDir();
  }

  @PostConstruct
  private void initLocalizationProducer() {
    // Load the commons langauge file, so it can be used as defaults provider for L10Maps
    commonLang = new Properties();
    try (InputStream stream = new FileInputStream(new File(langBase, "Common.properties"))) {
      commonLang.load(stream);

      logger.info("The current chat language is {} by {}", commonLang.get("Lang.info.displayName"), commonLang.get("Lang.info.creator"));
    } catch (IOException e) {
      logger.error("Failed loading Common language file!", e);
    }
  }

  @Produces
  public L10nMap createL10nMap(InjectionPoint injectionPoint) {
    Annotated annotated = injectionPoint.getAnnotated();
    String baseName;

    if (annotated.isAnnotationPresent(L10nData.class)) {
      baseName = annotated.getAnnotation(L10nData.class).value().getSimpleName();
    } else {
      baseName = injectionPoint.getBean().getBeanClass().getSimpleName();
    }

    File langFile = new File(langBase, baseName + ".properties");
    L10nMapImpl l10NMap = new L10nMapImpl(commonLang);

    try (InputStream stream = new FileInputStream(langFile)) {
      l10NMap.load(stream);
    } catch (IOException e) {
      // I18n data will be injected in all components,
      // but a component is not required to have a language definition
      // This implementation will return an empty L10nMap containing only able to supply common strings
    }

    return l10NMap;
  }
}
