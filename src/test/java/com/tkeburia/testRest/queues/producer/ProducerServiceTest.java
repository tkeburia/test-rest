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

package com.tkeburia.testRest.queues.producer;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.exception.MissingPropertyException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProducerServiceTest {

    private static final String BROKER_NAME = "broker1";
    private static final String QUEUE_NAME = "queue1";
    private static final String PAYLOAD = "Payload";
    private ProducerService producerService;

    @Mock
    private JmsTemplate jmsTemplate;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        ProducerProperties producerProperties = new ProducerProperties();
        producerProperties.setQueueNames(ImmutableMap.of(BROKER_NAME, QUEUE_NAME));
        producerService = new ProducerService(ImmutableMap.of(BROKER_NAME, jmsTemplate), producerProperties);
    }

    @Test
    public void shouldSendToQueueSuccessfully() {
        producerService.sendToQueue(BROKER_NAME, PAYLOAD);
        verify(jmsTemplate).convertAndSend(QUEUE_NAME, PAYLOAD);
    }

    @Test
    public void shouldThrowMissingPropertyExceptionWhenNoConfiguredJmsTemplateForBroker() {
        exception.expect(MissingPropertyException.class);
        producerService.sendToQueue("other", PAYLOAD);
    }


}