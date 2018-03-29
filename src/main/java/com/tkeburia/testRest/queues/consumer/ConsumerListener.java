package com.tkeburia.testRest.queues.consumer;

import com.tkeburia.testRest.annotation.LogMethodData;
import com.tkeburia.testRest.util.SchemaUtils;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.util.Map;

import static com.tkeburia.testRest.util.SchemaUtils.validateAgainstSchema;

@Component
public class ConsumerListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerListener.class);

    private final String schemaDir;
    private final Map<String, String> queueSchemaFileMappings;

    @Autowired
    public ConsumerListener(@Value("${schema.file.directory}") String schemaDir, Map<String, String> queueSchemaFileMappings) {
        this.schemaDir = schemaDir;
        this.queueSchemaFileMappings = queueSchemaFileMappings;
    }

    @LogMethodData
    @Override
    public void onMessage(Message message) {
        if (!(message instanceof ActiveMQTextMessage)) {
            LOG.warn("Expected message of class ActiveMQTextMessage but got {}", message.getClass().getName());
            return;
        }
        ActiveMQTextMessage messageToUse = (ActiveMQTextMessage) message;
        try {
            String messageString = messageToUse.getText();
            String destinationQueue = messageToUse.getDestination().getPhysicalName();
            final String schemaFileName = queueSchemaFileMappings.get(destinationQueue);
            validateAgainstSchema(messageString, schemaDir, schemaFileName);
        }
        catch (IOException e) {
            LOG.error("Error matching the configured schema!", e);
        }
        catch (JMSException e) {
            LOG.error("Error reading message content: ", e);
        }
    }
}

