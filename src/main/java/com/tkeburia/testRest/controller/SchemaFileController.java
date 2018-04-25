/*
 * Copyright 2018 Tornike Keburia <tornike.keburia@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tkeburia.testRest.controller;

import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.annotation.AfterReturning;
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
@RequestMapping("/test-rest/schemaFile")
public class SchemaFileController {

    private final String schemaDir;

    @Autowired
    public SchemaFileController(@Value("${schema.file.directory}")String schemaDir) {
        this.schemaDir = schemaDir;
    }

    @ApiOperation(
            value = "List existing schema files",
            httpMethod = "GET",
            notes = "Lists the currently existing schema files that the application can use to validate posted data")
    @RequestMapping(method = GET, produces = "application/json")
    public ResponseEntity<?> getResponseFiles() throws IOException {
        return new ResponseEntity<Object>(new JSONObject().put("files", getFilesList(schemaDir)).toString(), OK);
    }

    @ApiOperation(
            value = "POST a new schema file",
            httpMethod = "POST",
            notes = "This operation creates a schema file in the configured directory (`schema.file.directory`) that can be used by" +
                    "`MainController` methods to validate posted data")
    @RequestMapping(method = POST)
    public ResponseEntity<?> postSchemaFile(@RequestParam String fileName, @RequestBody byte[] fileContent) throws IOException {
        writeBytesToFile(schemaDir + fileName, fileContent);
        return new ResponseEntity<>(CREATED);
    }
}
