package nl.juraji.biliomi.utility.cdi.producers;

import nl.juraji.biliomi.model.core.VersionInfo;
import nl.juraji.biliomi.utility.cdi.annotations.qualifiers.AppData;
import org.joda.time.DateTime;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created by Juraji on 20-6-2017.
 * Biliomi v3
 */
public final class VersionInfoProducer {

    @Inject
    @AppData("biliomi.build.version")
    private String appVersion;

    @Inject
    @AppData("webclient.useragent")
    private String userAgent;

    @Inject
    @AppData("biliomi.build.timestamp")
    private String buildTimestamp;

    @Produces
    public VersionInfo getVersionInfo() {
        VersionInfo versionInfo = new VersionInfo();

        try {
            versionInfo.setVersion(appVersion);
            versionInfo.setUserAgent(userAgent);
            versionInfo.setBuildDate(new DateTime(buildTimestamp));
        } catch (IllegalArgumentException e) {
            // In development mode no sources will be filtered yet
            versionInfo.setVersion("DEVELOPMENT");
            versionInfo.setUserAgent("DEVELOPMENT");
            versionInfo.setBuildDate(new DateTime());
        }

        return versionInfo;
    }
}
