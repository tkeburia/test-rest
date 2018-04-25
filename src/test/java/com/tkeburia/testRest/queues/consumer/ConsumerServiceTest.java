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

package com.tkeburia.testRest.queues.consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.org.lidalia.slf4jext.Level.ERROR;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerServiceTest {

    private TestLogger TLOG = TestLoggerFactory.getTestLogger(ConsumerService.class);

    private ConsumerService consumerService;

    @Mock
    private MessageListener messageListener;

    @Mock
    private Connection connection1;

    @Mock
    private Connection connection2;

    @Mock
    private MessageConsumer messageConsumer1;

    @Mock
    private MessageConsumer messageConsumer2;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setup() {
        TLOG.clearAll();
        consumerService = new ConsumerService(messageListener, ImmutableMap
                .of("broker1", connection1, "broker2", connection2), ImmutableList
                .of(messageConsumer1, messageConsumer2));
    }

    @Test
    public void shouldSetMessageListenerOnAllConsumers() throws JMSException {
        consumerService.consume();
        verify(messageConsumer1).setMessageListener(messageListener);
        verify(messageConsumer2).setMessageListener(messageListener);
    }

    @Test
    public void shouldStartAllConnections() throws JMSException {
        consumerService.consume();
        verify(connection1).start();
        verify(connection2).start();
    }

    @Test
    public void shouldLogJMSException() throws JMSException {
        TLOG.setEnabledLevels(ERROR);
        doThrow(new JMSException("tt")).when(connection1).start();
        consumerService.consume();
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertTrue(TLOG.getAllLoggingEvents().get(0).getMessage().contains("Queue consumer error : "));
    }
}