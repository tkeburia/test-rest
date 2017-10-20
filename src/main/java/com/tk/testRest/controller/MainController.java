package com.tk.testRest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tk.testRest.MyRunnable;
import com.tk.testRest.dto.RequestWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/testRest")
public class MainController {
    @RequestMapping(method = GET)
    public ResponseEntity<?> getMe(
            @RequestParam(required = false, defaultValue = "200") Integer giveMe,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(valueOf(giveMe));
    }

    @RequestMapping(method = POST)
    public ResponseEntity<?> postMe(
            @RequestBody HashMap params,
            @RequestParam(required = false, defaultValue = "200") Integer giveMe,
            HttpServletRequest request
    ) throws JsonProcessingException {
        return new ResponseEntity<>(valueOf(giveMe).getReasonPhrase(), valueOf(giveMe));
    }


    @RequestMapping(value = "validated", method = POST)
    public ResponseEntity<?> postMett(
            @RequestBody @Valid RequestWrapper requestWrapper,
            HttpServletRequest request
    ) throws JsonProcessingException {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "slow")
    public Flux<String> getSlowly() {
        EmitterProcessor<String> stream = EmitterProcessor.create();
        final Flux<String> flux = stream.doOnNext(System.out::println).doOnComplete(() -> System.out.println("done!"));

        new Thread(getRunnable(stream)).start();

        return flux;
    }

    private Runnable getRunnable(final EmitterProcessor<String> stream) {
        return new MyRunnable<EmitterProcessor<String>>(stream) {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        if (i % 3 == 0) Thread.sleep(1000);
                    }
                    catch (InterruptedException ignored) { }
                    t.onNext("------ " + i);
                }
                t.onComplete();
            }
        };
    }
}


