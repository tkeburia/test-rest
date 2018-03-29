package com.tkeburia.testRest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.Integer.toBinaryString;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AppConfigTest {

    private AppConfig appConfig = new AppConfig();


    @Test
    public void shouldCreateObjectMapperWithCorrectConfig() {
        final ObjectMapper objectMapper = appConfig.objectMapper();
        final String serFeatures = toBinaryString(objectMapper.getSerializationConfig().getSerializationFeatures());
        // INDENT_OUTPUT has ordinal of '1' so we want to check it was enabled and second to last byte was set to 1
        assertEquals('1', serFeatures.charAt(serFeatures.length() - 2));
    }
}