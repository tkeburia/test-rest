package com.tkeburia.testRest.queues.consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerServiceTest {

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
}