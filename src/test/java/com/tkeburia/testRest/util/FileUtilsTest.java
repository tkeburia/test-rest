package com.tkeburia.testRest.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.tkeburia.testRest.util.FileUtils.getFileAsString;
import static com.tkeburia.testRest.util.FileUtils.getFilesList;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileUtilsTest {

    private final String TEMP_DIR = "./tmp/";
    private final String FILE_1 = "file1";
    private final String FILE_2 = "file2";

    @Before
    public void setup() throws IOException {
        new File(TEMP_DIR).mkdir();
        new File(TEMP_DIR, FILE_1).createNewFile();
        new File(TEMP_DIR, FILE_2).createNewFile();

        writeStringToFile(new File(TEMP_DIR, FILE_1), "{ \"response\" : \"as_expected\" }", UTF_8);
    }

    @After
    public void cleanup() {
        final File dir = new File(TEMP_DIR);
        if (dir.listFiles() != null) {
            Stream.of(dir.listFiles()).forEach(File::delete);
        }
        dir.delete();
    }

    @Test
    public void shouldGetFilesList() {
        final List<String> fileNames = getFilesList(TEMP_DIR).stream().map(File::getPath).collect(toList());

        assertEquals(2, fileNames.size());
        assertTrue(fileNames.contains(TEMP_DIR + FILE_1));
        assertTrue(fileNames.contains(TEMP_DIR + FILE_2));
    }

    @Test
    public void shouldReturnEmptyListWhenExceptionThrownDuringListing() {
        final List<String> fileNames = getFilesList("\0").stream().map(File::getPath).collect(toList());
        assertTrue(fileNames.isEmpty());
    }

    @Test
    public void shouldReturnFileContent() throws IOException {
        assertEquals("{ \"response\" : \"as_expected\" }", getFileAsString(TEMP_DIR, FILE_1));
    }

    @Test
    public void shouldReturnEmptyStringWhenFileNameIsNull() throws IOException {
        assertEquals("", getFileAsString(TEMP_DIR, null));
    }



}