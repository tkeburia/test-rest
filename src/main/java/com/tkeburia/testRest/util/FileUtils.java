package com.tkeburia.testRest.util;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.readFileToString;

public final class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    /**
     * lists all files (not directories) in a given path. If the given directoryPath doesn't exist, it tries to create one
     * instead of throwing an exception which is the underlying
     * {@link org.apache.commons.io.FileUtils#listFiles(File, IOFileFilter, IOFileFilter)} method's behaviour.
     * However, if the creation fails, this method will end up returning an empty list
     * @param directoryPath parent directoryPath to list all files from
     * @return collection of files inside given directoryPath
     */
    public static Collection<File> getFilesList(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) dir.mkdir();
        try {
            return listFiles(dir, TrueFileFilter.INSTANCE, null);
        } catch (IllegalArgumentException e) {
            LOG.error("Error listing files in {}: ", directoryPath, e);
        }
        return new ArrayList<>();
    }

    public static String getFileAsString(String dirPath, String fileName) throws IOException {
        if (fileName == null) return "";
        return readFileToString(new File(dirPath, fileName), UTF_8);
    }
}
