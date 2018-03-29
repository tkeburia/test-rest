package com.tkeburia.testRest.controller;

import com.tkeburia.testRest.queues.producer.ProducerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/test-rest/queues")
@ConditionalOnProperty(name="activemq.connections.enabled", havingValue="true")
public class QueueController {

    private final ProducerService producerService;

    @Autowired
    public QueueController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @ApiOperation(
            value = "Adds the given payload to the given queue",
            httpMethod = "POST",
            notes = "This operation takes the payload from the request and puts it to a queue with the given name")
    @RequestMapping(method = POST)
    public ResponseEntity<?> putMessageToQueue(
            @RequestBody HashMap params,
            @RequestParam String brokerName
    ){
        producerService.sendToQueue(brokerName, params);
        return new ResponseEntity<>(OK);
    }
}
