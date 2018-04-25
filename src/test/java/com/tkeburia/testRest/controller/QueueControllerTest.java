/*
 * Copyright 2018 Tornike Keburia <tornike.keburia@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tkeburia.testRest.controller;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.queues.producer.ProducerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"schema.file.directory=./src/test/resources", "activemq.connections.enabled=true"},  locations = "classpath:test.properties")
public class QueueControllerTest {

    private MockMvc testServer;

    @Mock
    ProducerService producerService;

    @Before
    public void setup() {
        testServer = MockMvcBuilders.standaloneSetup(new QueueController(producerService)).build();
    }

    @Test
    public void shouldInvokeProducerServiceWithCorrectArguments() throws Exception {
        testServer
                .perform(
                        post("/test-rest/queues?brokerName=testQueue")
                        .content("{ \"key\" : \"value\"}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(producerService).sendToQueue("testQueue", ImmutableMap.of("key", "value"));
    }

}