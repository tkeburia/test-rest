package com.tkeburia.testRest.queues.consumer;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tkeburia.testRest.util.QueueUtils.buildConnectionFactory;
import static com.tkeburia.testRest.util.QueueUtils.verifyProperties;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

@Configuration
public class ConsumerConfig {

    private final ConsumerProperties consumerProperties;

    @Autowired
    public ConsumerConfig(ConsumerProperties consumerProperties) {
        this.consumerProperties = consumerProperties;
    }

    @Bean
    public Map<String, Connection> consumerConnectionMap() throws JMSException {
        verifyProperties(consumerProperties);
        Map<String, Connection> result = new HashMap<>();
        for (String id : consumerProperties.getIds()) {
            result.put(id, createConnection(id));
        }
        return result;
    }

    @Bean
    public List<MessageConsumer> messageConsumerList() throws JMSException {
        List<MessageConsumer> result = new ArrayList<>();
        for (String id : consumerProperties.getIds()) {
            result.add(createConsumer(id));
        }
        return result;
    }

    private MessageConsumer createConsumer(String queueId) throws JMSException {
        final Session consumerSession = consumerConnectionMap().get(queueId).createSession(false, AUTO_ACKNOWLEDGE);
        final Destination consumerDestination = consumerSession.createQueue(consumerProperties.getQueueNames().get(queueId));
        return consumerSession.createConsumer(consumerDestination);
    }

    private Connection createConnection(String queueId) throws JMSException {
        final PooledConnectionFactory pooledConnectionFactoryConsumer = new PooledConnectionFactory();
        String uri = consumerProperties.getUris().get(queueId);
        String userName = consumerProperties.getUserNames().get(queueId);
        String password = consumerProperties.getPasswords().get(queueId);
        pooledConnectionFactoryConsumer.setConnectionFactory(buildConnectionFactory(uri, userName, password));
        return pooledConnectionFactoryConsumer.createConnection();
    }
}
