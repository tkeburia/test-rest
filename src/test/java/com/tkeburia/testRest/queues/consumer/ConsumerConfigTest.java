package com.tkeburia.testRest.queues.consumer;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.matchers.Equals;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

import java.util.List;
import java.util.Map;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ConsumerConfigTest {

    private static final String BROKER_NAME_1 = "broker1";
    private static final String BROKER_NAME_2 = "broker2";
    private static final String URI_1 = "tcp://uri1";
    private static final String USER_NAME_1 = "userName1";
    private static final String PASSWORD_1 = "password1";
    private static final String QUEUE_NAME_1 = "queueName1";
    private static final String USER_NAME_2 = "userName2";
    private static final String PASSWORD_2 = "password2";
    private static final String QUEUE_NAME_2 = "queueName2";
    private static final String URI_2 = "tcp://uri2";
    private ConsumerConfig consumerConfig;
    private ConsumerProperties consumerProperties;

    @MockBean
    private PooledConnectionFactory pooledConnectionFactory;

    @Mock
    private ActiveMQConnection connection;

    @Mock
    private ActiveMQSession session;

    @Mock
    private ActiveMQMessageConsumer consumer;

    @Before
    public void setup() {
        consumerProperties = new ConsumerProperties();
        consumerConfig = new ConsumerConfig(consumerProperties, pooledConnectionFactory);
    }

    @Test
    public void shouldCreateConsumerConnectionMap() throws JMSException {
        consumerProperties.setUris(ImmutableMap.of(BROKER_NAME_1, URI_1, BROKER_NAME_2, URI_2));
        consumerProperties.setUserNames(ImmutableMap.of(BROKER_NAME_1, USER_NAME_1, BROKER_NAME_2, USER_NAME_2));
        consumerProperties.setPasswords(ImmutableMap.of(BROKER_NAME_1, PASSWORD_1, BROKER_NAME_2, PASSWORD_2));
        consumerProperties.setQueueNames(ImmutableMap.of(BROKER_NAME_1, QUEUE_NAME_1, BROKER_NAME_2, QUEUE_NAME_2));
        final Map<String, Connection> connectionMap = consumerConfig.consumerConnectionMap();
        assertEquals(2, connectionMap.entrySet().size());
        verify(pooledConnectionFactory).setConnectionFactory(argThat(isConnectionFactoryWithDetails(URI_1, USER_NAME_1, PASSWORD_1)));
        verify(pooledConnectionFactory).setConnectionFactory(argThat(isConnectionFactoryWithDetails(URI_2, USER_NAME_2, PASSWORD_2)));
        verify(pooledConnectionFactory, times(2)).createConnection();
    }

    @Test
    public void shouldCreateConsumerList() throws JMSException {
        consumerProperties.setUris(ImmutableMap.of(BROKER_NAME_1, URI_1));
        consumerProperties.setUserNames(ImmutableMap.of(BROKER_NAME_1, USER_NAME_1));
        consumerProperties.setPasswords(ImmutableMap.of(BROKER_NAME_1, PASSWORD_1));
        consumerProperties.setQueueNames(ImmutableMap.of(BROKER_NAME_1, QUEUE_NAME_1));
        when(pooledConnectionFactory.createConnection()).thenReturn(connection);
        when(connection.createSession(false, AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createQueue(QUEUE_NAME_1)).thenReturn(new ActiveMQQueue(QUEUE_NAME_1));
        when(session.createConsumer(argThat(isDestinationWithName(QUEUE_NAME_1)))).thenReturn(consumer);
        final List<MessageConsumer> messageConsumers = consumerConfig.messageConsumerList();
        assertEquals(consumer, messageConsumers.get(0));
    }

    private ArgumentMatcher<ConnectionFactory> isConnectionFactoryWithDetails(String brokerUrl, String userName, String password) {
        return new ActiveMQConnectionFactoryDetailsMatcher(brokerUrl, userName, password);
    }

    private ArgumentMatcher<ActiveMQDestination> isDestinationWithName(String expected) {
        return new ActiveMQDestinationNameMatcher(expected);
    }

    @AllArgsConstructor
    private class ActiveMQDestinationNameMatcher implements ArgumentMatcher<ActiveMQDestination> {
        private String expected;
        @Override
        public boolean matches(ActiveMQDestination argument) {
            return argument.getPhysicalName().equals(expected);
        }
    }

    @AllArgsConstructor
    private class ActiveMQConnectionFactoryDetailsMatcher implements ArgumentMatcher<ConnectionFactory> {

        private String uri;
        private String userName;
        private String password;

        @Override
        public boolean matches(ConnectionFactory item) {
            if (!(item instanceof ActiveMQConnectionFactory)) {
                return false;
            }
            ActiveMQConnectionFactory itemToUse = (ActiveMQConnectionFactory) item;
            return itemToUse.getBrokerURL().equals(uri)
                    && itemToUse.getUserName().equals(userName)
                    && itemToUse.getPassword().equals(password);
        }
    }
}