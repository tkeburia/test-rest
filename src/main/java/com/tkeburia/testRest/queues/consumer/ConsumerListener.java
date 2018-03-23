package com.tkeburia.testRest.queues.consumer;

import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class ConsumerListener implements MessageListener{

    @Override
    public void onMessage(Message message) {

    }
}
