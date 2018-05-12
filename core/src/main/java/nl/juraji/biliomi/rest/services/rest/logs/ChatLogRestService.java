package nl.juraji.biliomi.rest.services.rest.logs;

import nl.juraji.biliomi.model.internal.rest.LogInfo;
import nl.juraji.biliomi.rest.config.Responses;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Created by Juraji on 25-8-2017.
 * Biliomi v3
 */
@Path("/logs")
public class ChatLogRestService {

    private final File loggingDir = new File(System.getProperty("user.dir") + "/logs");
    private final File archiveLoggingDir = new File(loggingDir, "archive/chat");

    @GET
    @Path("/latest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestChatLog() throws IOException {
        File currentChatLogFile = new File(loggingDir, "chat.log");
        if (!currentChatLogFile.exists()) {
            return Responses.noContent();
        }

        LogInfo logInfo = new LogInfo();
        logInfo.setLogDate(DateTime.now());
        logInfo.getLines().addAll(FileUtils.readLines(currentChatLogFile, "UTF-8"));

        return Responses.ok(logInfo);
    }

    @GET
    @Path("/archive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedLogFiles() {
        String[] files = archiveLoggingDir.list();
        List<String> collect = null;

        if (files != null) {
            collect = Arrays.stream(files)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        }

        return Responses.okOrEmpty(collect);
    }

    @GET
    @Path("/archive/{filename}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getArchivedLog(@PathParam("filename") String fileName) throws IOException {
        if (StringUtils.isEmpty(fileName)) {
            return Responses.noContent();
        }

        File logFile = new File(archiveLoggingDir, fileName);
        if (!logFile.exists()) {
            return Responses.noContent();
        }

        LogInfo logInfo = new LogInfo();
        logInfo.setLogDate(getFileCreationDateFromName(logFile));
        logInfo.getLines().addAll(readGzipFile(logFile));

        return Responses.ok(logInfo);
    }

    private List<String> readGzipFile(File file) throws IOException {
        GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(file));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream));
        String line;
        List<String> lines = new ArrayList<>();

        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    private DateTime getFileCreationDateFromName(File file) {
        return new DateTime(file.getName().substring(5, 15));
    }
}
