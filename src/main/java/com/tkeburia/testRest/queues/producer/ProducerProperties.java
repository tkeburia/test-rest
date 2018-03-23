package com.tkeburia.testRest.queues.producer;

import com.tkeburia.testRest.queues.QueueProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.tkeburia.testRest.constants.Constants.QUEUE_PRODUCER;

@Configuration
@ConfigurationProperties(prefix = QUEUE_PRODUCER)
public class ProducerProperties extends QueueProperties{
}
