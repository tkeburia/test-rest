package com.tkeburia.testRest.queues.producer;

import com.google.common.collect.ImmutableMap;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ProducerConfigTest {

    private static final String BROKER_NAME_1 = "broker1";
    private static final String QUEUE_NAME_1 = "queue1";
    private static final String BROKER_URI_1 = "tcp://broker.uri1";
    private static final String USER_NAME_1 = "userName1";
    private static final String PASSWORD_1 = "password1";
    private static final String BROKER_NAME_2 = "broker2";
    private static final String QUEUE_NAME_2 = "queue2";
    private static final String BROKER_URI_2 = "tcp://broker.uri2";
    private static final String USER_NAME_2 = "userName2";
    private static final String PASSWORD_2 = "password2";
    private ProducerConfig producerConfig;

    @Before
    public void setup() {
        ProducerProperties producerProperties = new ProducerProperties();
        producerProperties.setQueueNames(ImmutableMap.of(BROKER_NAME_1, QUEUE_NAME_1, BROKER_NAME_2, QUEUE_NAME_2));
        producerProperties.setUris(ImmutableMap.of(BROKER_NAME_1, BROKER_URI_1, BROKER_NAME_2, BROKER_URI_2));
        producerProperties.setUserNames(ImmutableMap.of(BROKER_NAME_1, USER_NAME_1, BROKER_NAME_2, USER_NAME_2));
        producerProperties.setPasswords(ImmutableMap.of(BROKER_NAME_1, PASSWORD_1, BROKER_NAME_2, PASSWORD_2));

        producerConfig = new ProducerConfig(producerProperties);
    }

    @Test
    public void shouldCreateJmsTemplateMapForCorrectProperties() {
        final Map<String, JmsTemplate> templateMap = producerConfig.jmsTemplateMap();

        assertEquals(2, templateMap.entrySet().size());
        assertEquals(QUEUE_NAME_2, templateMap.get(BROKER_NAME_2).getDefaultDestinationName());
        assertEquals(USER_NAME_2, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_2).getConnectionFactory()).getUserName());
        assertEquals(PASSWORD_2, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_2).getConnectionFactory()).getPassword());
        assertEquals(BROKER_URI_2, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_2).getConnectionFactory()).getBrokerURL());

        assertEquals(QUEUE_NAME_1, templateMap.get(BROKER_NAME_1).getDefaultDestinationName());
        assertEquals(USER_NAME_1, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_1).getConnectionFactory()).getUserName());
        assertEquals(PASSWORD_1, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_1).getConnectionFactory()).getPassword());
        assertEquals(BROKER_URI_1, ((ActiveMQConnectionFactory)templateMap.get(BROKER_NAME_1).getConnectionFactory()).getBrokerURL());
    }
}