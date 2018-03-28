package com.tkeburia.testRest.queues.consumer;

import com.tkeburia.testRest.queues.BrokerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.tkeburia.testRest.constants.Constants.BROKER_CONSUMER;

@Configuration
@ConfigurationProperties(prefix = BROKER_CONSUMER)
public class ConsumerProperties extends BrokerProperties{
}
