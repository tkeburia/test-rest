package com.tkeburia.testRest.queues.producer;

import com.tkeburia.testRest.queues.QueueProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.tkeburia.testRest.constants.Constants.BROKER_PRODUCER;

@Configuration
@ConfigurationProperties(prefix = BROKER_PRODUCER)
public class ProducerProperties extends QueueProperties{
}
