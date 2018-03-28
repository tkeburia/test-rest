package com.tkeburia.testRest.controller;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.queues.producer.ProducerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "schema.file.directory=./src/test/resources")
public class QueueControllerTest {

    @Autowired
    MockMvc testServer;

    @MockBean
    ProducerService producerService;

    @Test
    public void shouldInvokeProducerServiceWithCorrectArguments() throws Exception {
        testServer
                .perform(
                        post("/testRest/queues?queueId=testQueue")
                        .content("{ \"key\" : \"value\"}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(producerService).sendToQueue("testQueue", ImmutableMap.of("key", "value"));
    }

}