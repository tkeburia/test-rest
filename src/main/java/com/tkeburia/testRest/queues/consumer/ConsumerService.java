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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
public class ConsumerService {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

    private final MessageListener messageListener;
    private final Map<String, Connection> consumerConnectionMap;
    private final List<MessageConsumer> messageConsumerList;

    @Autowired
    public ConsumerService(
            MessageListener messageListener,
            @Qualifier("consumerConnectionMap") Map<String, Connection> consumerConnectionMap,
            @Qualifier("messageConsumerList") List<MessageConsumer> messageConsumerList
    ) {
        this.messageListener = messageListener;
        this.consumerConnectionMap = consumerConnectionMap;
        this.messageConsumerList = messageConsumerList;
    }

    public void consume(){
        try {
            for (MessageConsumer consumer : messageConsumerList){
                consumer.setMessageListener(messageListener);
            }
            for (Connection connection : consumerConnectionMap.values()) {
                connection.start();
            }
        }
        catch (JMSException e) {
            LOG.error("Queue consumer error : ", e);
        }
    }
}
