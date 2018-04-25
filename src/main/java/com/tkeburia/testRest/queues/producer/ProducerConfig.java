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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import java.util.Map;
import java.util.stream.Collectors;

import static com.tkeburia.testRest.constants.Constants.BROKER_PRODUCER;
import static com.tkeburia.testRest.util.QueueUtils.buildConnectionFactory;
import static com.tkeburia.testRest.util.QueueUtils.verifyProperties;
import static java.util.function.Function.identity;

@Configuration
public class ProducerConfig {

    private final ProducerProperties producerProperties;

    @Autowired
    public ProducerConfig(ProducerProperties producerProperties) {
        this.producerProperties = producerProperties;
    }

    @Bean
    @ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
    public Map<String, JmsTemplate> jmsTemplateMap() {
        verifyProperties(producerProperties, BROKER_PRODUCER);
        return producerProperties.getIds().stream().collect(Collectors.toMap(identity(), this::templateForBroker));
    }

    private JmsTemplate templateForBroker(String brokerName) {
        JmsTemplate template = new JmsTemplate();
        final String uri = producerProperties.getUris().get(brokerName);
        final String userName = producerProperties.getUserNames().get(brokerName);
        final String password = producerProperties.getPasswords().get(brokerName);
        template.setConnectionFactory(buildConnectionFactory(uri, userName, password));
        template.setDefaultDestinationName(producerProperties.getQueueNames().get(brokerName));
        return template;
    }
}

