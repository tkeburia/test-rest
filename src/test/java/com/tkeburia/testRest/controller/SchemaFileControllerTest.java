package com.tkeburia.testRest.controller;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.stream.Stream;

import static com.tkeburia.testRest.util.FileUtils.getFileAsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SchemaFileController.class)
@RunWith(SpringRunner.class)
public class SchemaFileControllerTest {

    private static final String FILE_NAME = "test_schema.json";
    private static final String FILE_NAME2 = "test_schema2.json";

    @Autowired
    MockMvc testServer;

    @Value("${schema.file.directory}")
    private String schemaDir;

    @After
    public void cleanup() {
        final File dir = new File(schemaDir);
        if (dir.listFiles() != null) {
            Stream.of(dir.listFiles()).forEach(File::delete);
        }
        dir.delete();
    }

    @Test
    public void shouldCreateNewFileWhenResponseDirExists() throws Exception {
        new File(schemaDir).mkdir();

        testServer.perform(
                post("/test-rest/schemaFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        assertTrue(new File(schemaDir + FILE_NAME).exists());
        assertEquals("{\"content\" : \"created\"}", getFileAsString(schemaDir, FILE_NAME));
    }

    @Test
    public void shouldCreateNewFileWhenResponseDirDoesntExist() throws Exception {
        testServer.perform(
                post("/test-rest/schemaFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        final File file = new File(schemaDir + FILE_NAME);
        assertTrue(new File(schemaDir + FILE_NAME).exists());
        assertEquals("{\"content\" : \"created\"}", getFileAsString(schemaDir, FILE_NAME));
    }

    @Test
    public void shouldListTwoExistingFiles() throws Exception {

        testServer.perform(
                post("/test-rest/schemaFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        testServer.perform(
                post("/test-rest/schemaFile?fileName=" + FILE_NAME2)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        testServer.perform(get("/test-rest/schemaFile"))
                  .andExpect(status().isOk())
                  .andExpect(content().string("{\"files\":[\"" + schemaDir + FILE_NAME + "\",\"" + schemaDir + FILE_NAME2 + "\"]}"));
    }

}