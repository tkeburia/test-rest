package com.tkeburia.testRest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        final FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        fileInputStream.close();
        return new String(bytes);
    }
}


