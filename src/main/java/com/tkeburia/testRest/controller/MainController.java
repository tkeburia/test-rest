package com.tkeburia.testRest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tkeburia.testRest.MyRunnable;
import com.tkeburia.testRest.dto.RequestWrapper;
import io.swagger.annotations.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/testRest")
@Api(value="testRest", description="Test operations that return values based on the provided request parameters or body")
public class MainController {

    private final String responseDir;

    @Autowired
    public MainController(@Value("${sample.response.directory}") String responseDir) {
        this.responseDir = responseDir;
    }


    @ApiOperation(
            value = "Test a GET request",
            httpMethod = "GET",
            notes = "The response Http satus is determined by the `giveMe` param (defaults to 200), " +
                    "if the `responseFile` param is present, it determines which of the sample " +
                    "response files to return in the response body")
    @RequestMapping(method = GET, produces = "application/json")
    public ResponseEntity<?> getMe(
            @RequestParam(required = false, defaultValue = "200") Integer giveMe,
            @RequestParam(required = false) String responseFile
    ) throws IOException {
        return new ResponseEntity<>(getFileAsString(responseFile), valueOf(giveMe));
    }

    @ApiParam()
    @ApiOperation(
            value = "Test a POST request",
            httpMethod = "POST",
            notes = "The response Http satus is determined by the `giveMe` param (defaults to 200), " +
                    "if the `responseFile` param is present, it determines which of the sample " +
                    "response files to return in the response body")
    @RequestMapping(method = POST, produces = "application/json")
    public ResponseEntity<?> postMe(
            @RequestBody HashMap params,
            @RequestParam(required = false, defaultValue = "200") Integer giveMe,
            @RequestParam(required = false) String responseFile
    ) throws IOException {
        return new ResponseEntity<>(getResponseMessage(giveMe, responseFile), valueOf(giveMe));
    }

    @ApiOperation(value = "", hidden = true)
    @RequestMapping(value = "validated", method = POST)
    public ResponseEntity<?> postMett(
            @RequestBody @Valid RequestWrapper requestWrapper,
            HttpServletRequest request
    ) throws JsonProcessingException {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "", hidden = true)
    @RequestMapping(value = "slow", method = GET)
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

    private String getResponseMessage(Integer giveMe, String responseFile) throws IOException {
        String message;
        if (responseFile == null) {
            message = new JSONObject().put("response", valueOf(giveMe).getReasonPhrase()).toString();
        } else {
            message = getFileAsString(responseFile);
        }
        return message;
    }

    private String getFileAsString(String fileName) throws IOException {
        if (fileName == null) return "";
        File file =  new File(responseDir + fileName);
        byte[] bytes = new byte[(int) file.length()];
        new FileInputStream(file).read(bytes);
        return new String(bytes);
    }
}


