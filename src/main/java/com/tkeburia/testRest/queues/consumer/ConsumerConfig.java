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

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tkeburia.testRest.constants.Constants.BROKER_CONSUMER;
import static com.tkeburia.testRest.util.QueueUtils.buildConnectionFactory;
import static com.tkeburia.testRest.util.QueueUtils.verifyProperties;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;

@Configuration
public class ConsumerConfig {

    private final ConsumerProperties consumerProperties;
    private final PooledConnectionFactory pooledConnectionFactory;

    @Autowired
    public ConsumerConfig(ConsumerProperties consumerProperties, PooledConnectionFactory pooledConnectionFactory) {
        this.consumerProperties = consumerProperties;
        this.pooledConnectionFactory = pooledConnectionFactory;
    }

    @Bean
    @ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
    public Map<String, Connection> consumerConnectionMap() throws JMSException {
        verifyProperties(consumerProperties, BROKER_CONSUMER);
        Map<String, Connection> result = new HashMap<>();
        for (String id : consumerProperties.getIds()) {
            result.put(id, createConnection(id));
        }
        return result;
    }

    @Bean
    @ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
    public List<MessageConsumer> messageConsumerList() throws JMSException {
        List<MessageConsumer> result = new ArrayList<>();
        for (String id : consumerProperties.getIds()) {
            result.add(createConsumer(id));
        }
        return result;
    }

    @Bean
    @ConfigurationProperties(prefix = "queue.schema.files.names")
    public Map<String, String> queueSchemaFileMappings() {
        return new HashMap<>();
    }

    @Bean
    @ConfigurationProperties(prefix = "queue.response.script.names")
    public Map<String, String> queueResponseScriptMappings() {
        return new HashMap<>();
    }

    @Bean
    @ConfigurationProperties(prefix = "queue.response.brokers")
    public Map<String, String> queueResponseDestinationMappings() {
        return new HashMap<>();
    }

    private MessageConsumer createConsumer(String brokerName) throws JMSException {
        final Session consumerSession = consumerConnectionMap().get(brokerName).createSession(false, AUTO_ACKNOWLEDGE);
        final Destination consumerDestination = consumerSession.createQueue(consumerProperties.getQueueNames().get(brokerName));
        return consumerSession.createConsumer(consumerDestination);
    }

    private Connection createConnection(String brokerName) throws JMSException {
        String uri = consumerProperties.getUris().get(brokerName);
        String userName = consumerProperties.getUserNames().get(brokerName);
        String password = consumerProperties.getPasswords().get(brokerName);
        pooledConnectionFactory.setConnectionFactory(buildConnectionFactory(uri, userName, password));
        return pooledConnectionFactory.createConnection();
    }
}
