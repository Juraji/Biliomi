package nl.juraji.biliomi.utility.types;

import nl.juraji.biliomi.BiliomiContainer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by Juraji on 7-5-2017.
 * Biliomi v3
 */
public abstract class Exporter {
    private static final String DEFAULT_EXPORT_DIR = "exports";
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final File targetFile;
    private final CSVPrinter printer;

    protected Exporter(String... headers) throws IOException {
        printer = new CSVPrinter(new StringBuilder(), CSVFormat.RFC4180.withHeader(headers));

        File workingDir = BiliomiContainer.getParameters().getWorkingDir(DEFAULT_EXPORT_DIR);
        targetFile = new File(workingDir, new MutableString()
                .append(getClass().getSimpleName())
                .append('_')
                .append(DateTime.now().toString("yyyy-MM-dd"))
                .append(".csv")
                .toString());
    }

    /**
     * The main method for row generation.
     * Use addRecord() to add rows to the printer
     */
    public abstract void generateRows();

    public abstract String getDoneMessage();

    /**
     * Save all records to file
     *
     * @throws IOException When an exception occurs during file access
     */
    public void save() throws IOException {
        String output = printer.getOut().toString();
        FileUtils.writeStringToFile(targetFile, output, CHARSET, false);
    }

    public File getTargetFile() {
        return targetFile;
    }

    /**
     * Add a new record to the printer
     *
     * @param data A value per column
     * @throws IOException When an error occurs during stream access
     */
    protected void addRecord(Object... data) throws IOException {
        printer.printRecord(data);
    }
}
