package com.tkeburia.testRest.queues.producer;

import com.tkeburia.testRest.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
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
    public Map<String, JmsTemplate> jmsTemplateMap() {
        verifyProperties(producerProperties, BROKER_PRODUCER);
        return producerProperties.getIds().stream().collect(Collectors.toMap(identity(), this::templateForQueue));
    }

    private JmsTemplate templateForQueue(String queueId) {
        JmsTemplate template = new JmsTemplate();
        final String uri = producerProperties.getUris().get(queueId);
        final String userName = producerProperties.getUserNames().get(queueId);
        final String password = producerProperties.getPasswords().get(queueId);
        template.setConnectionFactory(buildConnectionFactory(uri, userName, password));
        template.setDefaultDestinationName(producerProperties.getQueueNames().get(queueId));
        return template;
    }
}

