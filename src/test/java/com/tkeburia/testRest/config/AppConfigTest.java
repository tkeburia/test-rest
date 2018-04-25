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