package nl.juraji.biliomi.io.web;

import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Created by Juraji on 19-4-2017.
 * Biliomi v3
 */
public final class SslOverTlsContextFactory extends SslContextFactory {

    public SslOverTlsContextFactory() {
        super(false);
        this.setIncludeProtocols("TLSv1.2");
    }
}
