package com.tkeburia.testRest.util;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

public final class SchemaUtils {

    private SchemaUtils() {

    }

    public static void validateAgainstSchema(String input, String schemaDir, String schemaFileName) throws IOException {
        if (schemaDir == null || schemaFileName == null) return;

        File schemaFile = new File(schemaDir, schemaFileName);

        JSONObject inputObject = new JSONObject(input);
        JSONObject jsonSchema = new JSONObject(FileUtils.readFileToString(schemaFile, UTF_8));

        final Schema schema = SchemaLoader.load(jsonSchema);
        schema.validate(inputObject);
    }

    public static void writeBytesToFile(String filePath, byte[] input) throws IOException {
        writeByteArrayToFile(new File(filePath), input);
    }

}
