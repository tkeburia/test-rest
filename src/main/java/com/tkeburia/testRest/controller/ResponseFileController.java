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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/testRest/responseFile")
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
        return new ResponseEntity<Object>(new JSONObject().put("files", getFileList()).toString(), OK);
    }


    @ApiOperation(
            value = "POST a new sample response file",
            httpMethod = "POST",
            notes = "This operation creates a file in the configured directory (`sample.response.directory`) that can be returned by" +
                    "`MainController` methods if their name is provided in the request")
    @RequestMapping(method = POST)
    public ResponseEntity<?> postResponseFile(@RequestParam String fileName, @RequestBody byte[] fileContent) throws IOException {
        writeFile(fileName, fileContent);
        return new ResponseEntity<>(OK);
    }

    private List<File> getFileList() {
        final File dir = new File(responseDir);
        if (!dir.exists()) new File(responseDir).mkdir();
        final File[] responseFileDir = dir.listFiles();
        return Stream.of(responseFileDir).filter(File::isFile).collect(Collectors.toList());
    }

    private void writeFile(String fileName, byte[] fileContent) throws IOException {
        final File dir = new File(responseDir);
        if (!dir.exists()) dir.mkdir();
        final FileOutputStream fileOutputStream = new FileOutputStream(responseDir + fileName);
        fileOutputStream.write(fileContent);
        fileOutputStream.close();
    }
}
