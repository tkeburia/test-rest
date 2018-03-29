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

