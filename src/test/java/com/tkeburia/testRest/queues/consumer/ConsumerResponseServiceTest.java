package com.tkeburia.testRest.queues.consumer;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.queues.producer.ProducerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jms.JMSException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.org.lidalia.slf4jext.Level.ERROR;
import static uk.org.lidalia.slf4jext.Level.WARN;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerResponseServiceTest {

    private TestLogger TLOG = TestLoggerFactory.getTestLogger(ConsumerResponseService.class);

    private static final String MESSAGE_TEXT = "messageText";
    private static final String FAILING_MESSAGE_TEXT = "failMe";
    private ConsumerResponseService consumerResponseService;

    @Mock
    private ProducerService producerService;

    @Mock
    private ActiveMQTextMessage message;

    @Before
    public void setup() {
        consumerResponseService = new ConsumerResponseService(
                "src/test/resources",
                ImmutableMap.of("testDestination", "testScript.groovy", "exceptionDestination", "exceptionScript.groovy"),
                ImmutableMap.of("testDestination", "responseBroker"),
                producerService
                );

        TLOG.clearAll();
    }


    @Test
    public void shouldRespondToQueueMessageSuccessfully() throws IOException, JMSException {
        when(message.getText()).thenReturn(MESSAGE_TEXT);
        consumerResponseService.respondToQueueMessage(message, "testDestination");
        verify(producerService).sendToQueue("responseBroker", MESSAGE_TEXT + "-processed");
    }

    @Test
    public void shouldNotSendResponseAndLogErrorWhenResponseObjectNull() throws IOException, JMSException {
        TLOG.setEnabledLevels(WARN);
        when(message.getText()).thenReturn(FAILING_MESSAGE_TEXT);
        consumerResponseService.respondToQueueMessage(message, "testDestination");
        verify(producerService, never()).sendToQueue(anyString(), any());
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertEquals("Queue response script returned a null value and will be ignored", TLOG.getAllLoggingEvents().get(0).getMessage());
    }

    @Test
    public void shouldLogScriptExceptionAndReturnNull() throws IOException, JMSException {
        TLOG.setEnabledLevels(WARN);
        when(message.getText()).thenReturn(FAILING_MESSAGE_TEXT);
        consumerResponseService.respondToQueueMessage(message, "exceptionDestination");
        verify(producerService, never()).sendToQueue(anyString(), any());
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertEquals("Queue response script returned a null value and will be ignored", TLOG.getAllLoggingEvents().get(0).getMessage());
    }
}