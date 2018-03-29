package com.tkeburia.testRest.controller;

import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.tkeburia.testRest.util.FileUtils.getFilesList;
import static com.tkeburia.testRest.util.SchemaUtils.writeBytesToFile;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/test-rest/responseFile")
public class ResponseFileController {

    private final String responseDir;

    @Autowired
    public ResponseFileController(@Value("${sample.response.directory}") String responseDir) {
        this.responseDir = responseDir;
    }

    @ApiOperation(
            value = "List existing sample response files",
            httpMethod = "GET",
            notes = "Lists the currently existing sample response files that the application can return if the file name is provided")
    @RequestMapping(method = GET, produces = "application/json")
    public ResponseEntity<?> getResponseFiles() throws IOException {
        return new ResponseEntity<Object>(new JSONObject().put("files", getFilesList(responseDir)).toString(), OK);
    }


    @ApiOperation(
            value = "POST a new sample response file",
            httpMethod = "POST",
            notes = "This operation creates a file in the configured directory (`sample.response.directory`) that can be returned by" +
                    "`MainController` methods if their name is provided in the request")
    @RequestMapping(method = POST)
    public ResponseEntity<?> postResponseFile(@RequestParam String fileName, @RequestBody byte[] fileContent) throws IOException {
        writeBytesToFile(responseDir + fileName, fileContent);
        return new ResponseEntity<>(CREATED);
    }


}
