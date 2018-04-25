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

package com.tkeburia.testRest.util;

import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.exception.DetailedValidationException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

public final class SchemaUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SchemaUtils.class);

    private SchemaUtils() {

    }

    public static void validateAgainstSchema(String input, String schemaDir, String schemaFileName) throws IOException {
        if (schemaDir == null || schemaFileName == null) return;

        File schemaFile = new File(schemaDir, schemaFileName);

        JSONObject inputObject = new JSONObject(input);
        JSONObject jsonSchema = new JSONObject(readFileToString(schemaFile, UTF_8));

        final Schema schema = SchemaLoader.load(jsonSchema);
        try {
            schema.validate(inputObject);
        }
        catch (ValidationException e) {
            throw new DetailedValidationException(e);
        }
    }

    public static void writeBytesToFile(String filePath, byte[] input) throws IOException {
        writeByteArrayToFile(new File(filePath), input);
    }

    public static Object buildQueueResponseForMessage(String inputMessage, String scriptDir, String responseScript) throws IOException {

        GroovyShell groovyShell = new GroovyShell(new Binding(ImmutableMap.of("inputMessage", inputMessage)));

        final File scriptFile = new File(scriptDir, responseScript);
        try {
            final Script script = groovyShell.parse(scriptFile);
            return script.run();
        }
        catch (Exception e) {
            LOG.error("Error executing script {}: ", scriptFile.getAbsolutePath(), e);
        }

        return null;
    }
}
