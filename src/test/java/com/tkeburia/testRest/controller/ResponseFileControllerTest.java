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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResponseFileController.class)
@RunWith(SpringRunner.class)
public class ResponseFileControllerTest {

    private static final String FILE_NAME = "test_file.json";
    private static final String FILE_NAME2 = "test_file2.json";

    @Autowired
    MockMvc testServer;

    @Value("${sample.response.directory}")
    private String responseDir;


    @After
    public void cleanup() {
        final File dir = new File(responseDir);
        if (dir.listFiles() != null) {
            Stream.of(dir.listFiles()).forEach(File::delete);
        }
        dir.delete();
    }

    @Test
    public void shouldCreateNewFileWhenResponseDirExists() throws Exception {
        new File(responseDir).mkdir();

        testServer.perform(
                post("/testRest/responseFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        final File file = new File(responseDir + FILE_NAME);
        assertTrue(file.exists());
        assertEquals("{\"content\" : \"created\"}", readFileAsString(file));
    }

    @Test
    public void shouldCreateNewFileWhenResponseDirDoesntExist() throws Exception {
        testServer.perform(
                post("/testRest/responseFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        final File file = new File(responseDir + FILE_NAME);
        assertTrue(file.exists());
        assertEquals("{\"content\" : \"created\"}", readFileAsString(file));
    }

    @Test
    public void shouldListTwoExistingFiles() throws Exception {

        testServer.perform(
                post("/testRest/responseFile?fileName=" + FILE_NAME)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        testServer.perform(
                post("/testRest/responseFile?fileName=" + FILE_NAME2)
                        .content("{\"content\" : \"created\"}")
        )
                  .andExpect(status().isCreated());

        testServer.perform(get("/testRest/responseFile"))
                  .andExpect(status().isOk())
                    .andExpect(content().string("{\"files\":[\"" + responseDir + FILE_NAME + "\",\"" + responseDir + FILE_NAME2 + "\"]}"));
    }

    private String readFileAsString(File file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        final FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return new String(bytes);
    }
}
