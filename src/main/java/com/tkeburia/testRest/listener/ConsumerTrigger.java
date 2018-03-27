package com.tkeburia.testRest.listener;

import com.tkeburia.testRest.queues.consumer.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConsumerTrigger implements ApplicationListener<ContextRefreshedEvent> {

    private final ConsumerService consumerService;

    @Autowired
    public ConsumerTrigger(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent) {
        consumerService.consume();
    }
}
