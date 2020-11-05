package com.log.finder.logfinder.controller;

import com.log.finder.logfinder.model.LogFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
public class LogFilesController {

    Logger logger = LoggerFactory.getLogger(LogFilesController.class);

    @Value("${path.log}")
    private String logFilePath;

    @GetMapping
    public List<LogFile> allLogFiles() throws IOException {

        logger.debug("Call allLogFiles method");


        return getLogFiles();

    }

    @RequestMapping(path = "/{logName}", method = RequestMethod.GET, produces = "application/zip")
    public byte[] findByLog(HttpServletResponse response, @PathVariable String logName) throws IOException {
        logger.debug("Call findByLog method");

        response.addHeader("Content-Disposition","attachment; filename=\""+logName+"\"");

        File theFile = returnTheGoodFile(logName);

        return getBytesFromFile(theFile);


    }

    private List<LogFile> getLogFiles() throws IOException {
        final List<LogFile> list = new ArrayList();

        try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logFilePath), path -> path.toString().contains(".log"))) {

            for(final Path p : directoryStream){

                final LogFile logFile = new LogFile();
                logFile.setName(p.getFileName().toString());
                logFile.setPath(p.toAbsolutePath().toString());
                logFile.setDate(new Date(p.toFile().lastModified()));
                list.add(logFile);
            }
        }


        return list;
    }

    public File returnTheGoodFile(String fileName) throws IOException {
        File theSearchFile = null;

        try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(logFilePath), path -> path.toString().contains(".log"))) {

            for(final Path p : directoryStream) {
                theSearchFile = p.toFile();
            }



            }


            return theSearchFile;
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

}
