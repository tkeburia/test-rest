package com.tkeburia.testRest.queues.consumer;

import com.tkeburia.testRest.queues.producer.ProducerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.Map;

import static com.tkeburia.testRest.util.SchemaUtils.buildQueueResponseForMessage;

@Component
@ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
public class ConsumerResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerResponseService.class);

    private final String scriptDir;
    private final Map<String, String> queueResponseScriptMappings;
    private final Map<String, String> queueResponseDestinationMappings;
    private final ProducerService producerService;

    @Autowired
    public ConsumerResponseService(
            @Value("${response.script.directory}") String scriptDir,
            Map<String, String> queueResponseScriptMappings,
            Map<String, String> queueResponseDestinationMappings,
            ProducerService producerService
    ) {
        this.scriptDir = scriptDir;
        this.queueResponseScriptMappings = queueResponseScriptMappings;
        this.queueResponseDestinationMappings = queueResponseDestinationMappings;
        this.producerService = producerService;
    }

    public void respondToQueueMessage(ActiveMQTextMessage mqTextMessage, String destination) throws JMSException, IOException {
        final String responseScript = queueResponseScriptMappings.get(destination);
        final Object o = buildQueueResponseForMessage(mqTextMessage.getText(), scriptDir, responseScript);
        final String responseDestination = queueResponseDestinationMappings.get(destination);
        if (o == null) {
            LOG.warn("Queue response script returned a null value and will be ignored");
            return;
        }
        producerService.sendToQueue(responseDestination, o);
    }
}
