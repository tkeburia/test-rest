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
