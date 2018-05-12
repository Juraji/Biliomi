package nl.juraji.biliomi.utility.factories.archives;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by Juraji on 6-10-2017.
 * Biliomi
 */
public class TarArchiveUtils {
    private TarArchiveUtils() {
        // private constructor
    }

    /**
     * Unpack the contents of a tarball (.tar.gz)
     *
     * @param tarball The source tarbal
     */
    public static void extract(File tarball) throws IOException {

        TarArchiveInputStream tarIn = new TarArchiveInputStream(
                new GzipCompressorInputStream(
                        new BufferedInputStream(
                                new FileInputStream(tarball))));

        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();

        while (tarEntry != null) {
            File entryDestination = new File(tarball.getParent(), tarEntry.getName());
            FileUtils.forceMkdirParent(entryDestination);

            if (tarEntry.isDirectory()) {
                FileUtils.forceMkdir(entryDestination);
            } else {
                entryDestination.createNewFile();

                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryDestination));
                byte[] buffer = new byte[1024];
                int len;

                while ((len = tarIn.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.close();
            }

            tarEntry = tarIn.getNextTarEntry();
        }

        tarIn.close();
    }
}
