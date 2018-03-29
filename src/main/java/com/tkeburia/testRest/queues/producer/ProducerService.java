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
