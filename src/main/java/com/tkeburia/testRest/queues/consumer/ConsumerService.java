package com.tkeburia.testRest.queues.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import java.util.List;
import java.util.Map;

@Service
public class ConsumerService {

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
            e.printStackTrace();
        }
    }
}
