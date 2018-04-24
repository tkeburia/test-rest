package com.tkeburia.testRest.queues.consumer;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.exception.DetailedValidationException;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.org.lidalia.slf4jext.Level.ERROR;
import static uk.org.lidalia.slf4jext.Level.WARN;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerListenerTest {

    private TestLogger TLOG = TestLoggerFactory.getTestLogger(ConsumerListener.class);

    @Mock
    private ActiveMQTextMessage message = new ActiveMQTextMessage();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private Message nonMqMessage;

    @Mock
    ConsumerResponseService consumerResponseService;

    private ConsumerListener consumerListener;

    @Before
    public void setup() throws MessageNotWriteableException {
        consumerListener = new ConsumerListener("./src/test/resources", ImmutableMap.of("queue1", "schema.json"), consumerResponseService);
        when(message.getDestination()).thenReturn(new ActiveMQQueue("queue1"));
        TLOG.clearAll();
    }

    @Test
    public void shouldValidateMessageSuccessfully() throws JMSException {
        when(message.getText()).thenReturn("{\"firstName\" : \"Peter\", \"lastName\" : \"Griffin\"}");
        consumerListener.onMessage(message);
        verify(message).getText();
        verify(message).getDestination();
    }

    @Test
    public void shouldFailValidationWithIncorrectData() throws JMSException {
        exception.expect(DetailedValidationException.class);
        when(message.getText()).thenReturn("{\"firstName\" : \"Peter\", \"surName\" : \"Griffin\"}");
        consumerListener.onMessage(message);
    }

    @Test
    public void shouldLogAndReturnWhenMessageNotActiveMQTextMessage() {
        TLOG.setEnabledLevels(WARN);
        consumerListener.onMessage(nonMqMessage);
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertTrue(TLOG.getLoggingEvents().get(0).getMessage().contains("Expected message of class ActiveMQTextMessage but got codegen.javax.jms.Message"));
    }

    @Test
    public void shouldLogJMSException() throws JMSException {
        TLOG.setEnabledLevels(ERROR);
        when(message.getText()).thenThrow(new JMSException("test"));
        consumerListener.onMessage(message);
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertTrue(TLOG.getAllLoggingEvents().get(0).getMessage().contains("Error reading message content"));
    }

    @Test
    public void shouldLogIOException() throws JMSException {
        TLOG.setEnabledLevels(ERROR);
        // invalid file name will cause an IOException that we need for the test
        consumerListener = new ConsumerListener("./src/test/resources", ImmutableMap.of("queue1", "\0"), consumerResponseService);
        when(message.getText()).thenReturn("{\"firstName\" : \"Peter\", \"surName\" : \"Griffin\"}");
        consumerListener.onMessage(message);
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertTrue(TLOG.getAllLoggingEvents().get(0).getMessage().contains("Error processing message:"));
    }
}