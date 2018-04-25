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

import com.tkeburia.testRest.exception.MissingPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
public class ProducerService {

    private final Map<String, JmsTemplate> jmsTemplateMap;
    private final ProducerProperties producerProperties;

    @Autowired
    public ProducerService(@Qualifier("jmsTemplateMap") Map<String, JmsTemplate> jmsTemplateMap, ProducerProperties producerProperties) {
        this.jmsTemplateMap = jmsTemplateMap;
        this.producerProperties = producerProperties;
    }

    public void sendToQueue(String brokerName, Object payload) {
        if (!jmsTemplateMap.keySet().contains(brokerName)) {
            throw new MissingPropertyException(String.format("No configuration found for queue with id '%s'", brokerName));
        }
        jmsTemplateMap.get(brokerName).convertAndSend(producerProperties.getQueueNames().get(brokerName), payload);
    }
}
