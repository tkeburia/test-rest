package com.tkeburia.testRest.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
@RunWith(SpringRunner.class)
public class MainControllerTest {

    private static final String FILE_NAME = "test_file.json";

    @Autowired
    MockMvc testServer;

    @Value("${sample.response.directory}")
    private String responseDir;

    @Before
    public void setup() throws IOException {
        final File dir = new File(responseDir);
        if (!dir.exists()) dir.mkdir();
        final FileOutputStream fileOutputStream = new FileOutputStream(responseDir + FILE_NAME);
        fileOutputStream.write("{ \"response\" : \"as_expected\" }".getBytes());
        fileOutputStream.close();
    }

    @After
    public void cleanup() {
        final File dir = new File(responseDir);
        if (dir.listFiles() != null) {
            Stream.of(dir.listFiles()).forEach(File::delete);
        }
        dir.delete();
    }

    @Test
    public void shouldReturn200AndEmptyBodyOnGetByDefault() throws Exception {
        testServer
                .perform(get("/testRest"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn400AndEmptyBodyOnGetWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=400"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn503AndEmptyBodyOnGetWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=503"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn201AndExpectedBodyOnGetWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=201&responseFile=" + FILE_NAME))
                .andExpect(status().isCreated())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn400AndExpectedBodyOnGetWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=400&responseFile=" + FILE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn200AndEmptyBodyOnPostByDefault() throws Exception {
        testServer
                .perform(
                        post("/testRest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"response\":\"OK\"}"));
    }

    @Test
    public void shouldReturn400AndEmptyBodyOnPostWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=400")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"response\":\"Bad Request\"}"));
    }

    @Test
    public void shouldReturn503AndEmptyBodyOnPostWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=503")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("{\"response\":\"Service Unavailable\"}"));
    }

    @Test
    public void shouldReturn201AndExpectedBodyOnPostWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=201&responseFile=" + FILE_NAME)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn400AndExpectedBodyOnPostWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=400&responseFile=" + FILE_NAME)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }




}