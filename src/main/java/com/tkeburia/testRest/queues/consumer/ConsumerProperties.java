package com.tkeburia.testRest.queues.consumer;

import com.tkeburia.testRest.queues.QueueProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.tkeburia.testRest.constants.Constants.QUEUE_CONSUMER;

@Configuration
@ConfigurationProperties(prefix = QUEUE_CONSUMER)
public class ConsumerProperties extends QueueProperties{
}
