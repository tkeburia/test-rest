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
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageNotWriteableException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerListenerTest {

    @Spy
    private ActiveMQTextMessage message = new ActiveMQTextMessage();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private Message nonMqMessage;

    private ConsumerListener consumerListener;

    @Before
    public void setup() throws MessageNotWriteableException {
        consumerListener = new ConsumerListener("./src/test/resources", ImmutableMap.of("queue1", "schema.json"));
        message.setDestination(new ActiveMQQueue("queue1"));
    }

    @Test
    public void shouldValidateMessageSuccessfully() throws JMSException {
        message.setText("{\"firstName\" : \"Peter\", \"lastName\" : \"Griffin\"}");
        consumerListener.onMessage(message);
        verify(message).getText();
        verify(message).getDestination();
    }

    @Test
    public void shouldFailValidationWithIncorrectData() throws MessageNotWriteableException {
        exception.expect(DetailedValidationException.class);
        message.setText("{\"firstName\" : \"Peter\", \"surName\" : \"Griffin\"}");
        consumerListener.onMessage(message);
    }
}