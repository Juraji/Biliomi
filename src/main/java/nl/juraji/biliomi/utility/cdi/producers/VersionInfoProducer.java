package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import org.joda.time.DateTime;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Properties;

/**
 * Created by Juraji on 20-6-2017.
 * Biliomi v3
 */
public final class VersionInfoProducer {

  @Inject
  @AppData
  private Properties appData;

  @Produces
  public VersionInfo getVersionInfo() {
    VersionInfo versionInfo = new VersionInfo();

    try {
      versionInfo.setVersion(appData.getProperty("biliomi.build.version"));
      versionInfo.setUserAgent(appData.getProperty("webclient.useragent"));
      versionInfo.setBuildDate(new DateTime(appData.getProperty("biliomi.build.timestamp")));
    } catch (IllegalArgumentException e) {
      // In development mode no sources will be filtered yet
      versionInfo.setVersion("DEVELOPMENT");
      versionInfo.setUserAgent("DEVELOPMENT");
      versionInfo.setBuildDate(new DateTime());
    }

    return versionInfo;
  }
}
