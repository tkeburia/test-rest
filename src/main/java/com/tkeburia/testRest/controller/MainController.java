package com.tkeburia.testRest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkeburia.testRest.util.FileUtils;
import com.tkeburia.testRest.util.SchemaUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.tkeburia.testRest.util.FileUtils.getFileAsString;
import static com.tkeburia.testRest.util.SchemaUtils.validateAgainstSchema;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.springframework.http.HttpStatus.valueOf;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/testRest")
@Api(value="testRest", description="Test operations that return values based on the provided request parameters or body")
public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);


    private final String responseDir;
    private final String schemaDir;
    private final ObjectMapper om;


    @Autowired
    public MainController(
            @Value("${sample.response.directory}") String responseDir,
            @Value("${schema.file.directory}") String schemaDir,
            ObjectMapper om
    ) {
        this.responseDir = responseDir;
        this.schemaDir = schemaDir;
        this.om = om;
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
        return new ResponseEntity<>(getFileAsString(responseDir, responseFile), valueOf(giveMe));
    }

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
            @RequestParam(required = false) String responseFile,
            @RequestParam(required = false) String schemaFile
    ) throws IOException {

        validateAgainstSchema(om.writeValueAsString(params), schemaDir, schemaFile);
        return new ResponseEntity<>(getResponseMessage(giveMe, responseFile), valueOf(giveMe));
    }

    private String getResponseMessage(Integer giveMe, String responseFile) {
        final String altResponse = new JSONObject().put("response", valueOf(giveMe).getReasonPhrase()).toString();
        if (responseFile == null) return altResponse;
        try {
            return getFileAsString(responseDir, responseFile);
        }
        catch (IOException e) {
            LOG.error("Error getting altResponse message : {}", e);
        }
        return altResponse;
    }

}

