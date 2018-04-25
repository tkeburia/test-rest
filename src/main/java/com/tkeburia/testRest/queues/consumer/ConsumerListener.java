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

import com.tkeburia.testRest.annotation.LogMethodData;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.util.Map;

import static com.tkeburia.testRest.util.SchemaUtils.validateAgainstSchema;

@Component
@ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
public class ConsumerListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerListener.class);

    private final String schemaDir;
    private final Map<String, String> queueSchemaFileMappings;
    private final ConsumerResponseService consumerResponseService;

    @Autowired
    public ConsumerListener(
            @Value("${schema.file.directory}") String schemaDir,
            Map<String, String> queueSchemaFileMappings,
            ConsumerResponseService consumerResponseService) {
        this.schemaDir = schemaDir;
        this.queueSchemaFileMappings = queueSchemaFileMappings;
        this.consumerResponseService = consumerResponseService;
    }

    @LogMethodData
    @Override
    public void onMessage(Message message) {
        if (!(message instanceof ActiveMQTextMessage)) {
            LOG.warn(String.format("Expected message of class ActiveMQTextMessage but got %s", message.getClass().getName()));
            return;
        }
        ActiveMQTextMessage messageToUse = (ActiveMQTextMessage) message;
        try {
            String messageString = messageToUse.getText();
            String destination = messageToUse.getDestination().getPhysicalName();
            final String schemaFileName = queueSchemaFileMappings.get(destination);
            validateAgainstSchema(messageString, schemaDir, schemaFileName);
            consumerResponseService.respondToQueueMessage(messageToUse, destination);
        }
        catch (IOException e) {
            LOG.error("Error processing message:", e);
        }
        catch (JMSException e) {
            LOG.error("Error reading message content: ", e);
        }
    }
}

