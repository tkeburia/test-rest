package com.tkeburia.testRest.controller;

import com.tkeburia.testRest.exception.DetailedValidationException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "schema.file.directory=./src/test/resources")
public class MainControllerTest {

    private static final String FILE_NAME = "test_file.json";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    MockMvc testServer;

    @Value("${sample.response.directory}")
    private String responseDir;

    @Before
    public void setup() throws IOException {
        final File dir = new File(responseDir);
        if (!dir.exists()) dir.mkdir();
        writeStringToFile(new File(dir, FILE_NAME), "{ \"response\" : \"as_expected\" }", UTF_8);
    }

    @After
    public void cleanup() {
        final File dir = new File(responseDir);
        if (dir.listFiles() != null) {
            Stream.of(dir.listFiles()).forEach(File::delete);
        }
        dir.delete();
    }

    @Test
    public void shouldReturn200AndEmptyBodyOnGetByDefault() throws Exception {
        testServer
                .perform(get("/testRest"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn400AndEmptyBodyOnGetWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=400"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn503AndEmptyBodyOnGetWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=503"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturn201AndExpectedBodyOnGetWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=201&responseFile=" + FILE_NAME))
                .andExpect(status().isCreated())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn400AndExpectedBodyOnGetWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(get("/testRest?giveMe=400&responseFile=" + FILE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn200AndEmptyBodyOnPostByDefault() throws Exception {
        testServer
                .perform(
                        post("/testRest")
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"response\":\"OK\"}"));
    }

    @Test
    public void shouldReturn400AndEmptyBodyOnPostWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=400")
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"response\":\"Bad Request\"}"));
    }

    @Test
    public void shouldReturn503AndEmptyBodyOnPostWhenGiveMeValueProvided() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=503")
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("{\"response\":\"Service Unavailable\"}"));
    }

    @Test
    public void shouldReturn201AndExpectedBodyOnPostWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=201&responseFile=" + FILE_NAME)
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldReturn400AndExpectedBodyOnPostWhenGiveMeAndResponseFileGiven() throws Exception {
        testServer
                .perform(
                        post("/testRest?giveMe=400&responseFile=" + FILE_NAME)
                                .contentType(APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{ \"response\" : \"as_expected\" }"));
    }

    @Test
    public void shouldValidatePayloadAgainstSchema() throws Exception {
        testServer
                .perform(
                        post("/testRest?schemaFile=schema.json")
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstName\": \"Peter\", \"lastName\": \"Griffin\"}")
                )
                .andExpect(status().isOk());

    }

    @Test
    public void shouldValidatePayloadWithOptionalPropertiesAgainstSchema() throws Exception {
        testServer
                .perform(
                        post("/testRest?schemaFile=schema.json")
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstName\": \"Peter\", \"lastName\": \"Griffin\", \"age\": 42}")
                )
                .andExpect(status().isOk());

    }

    @Test
    public void shouldValidatePayloadWithUnknownProperties() throws Exception {
        testServer
                .perform(
                        post("/testRest?schemaFile=schema.json")
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstName\": \"Peter\", \"lastName\": \"Griffin\", \"spouse\": \"Lois Griffin\"}")
                )
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailValidationForIncorrectDataType() throws Exception {
        exception.expect(nestedValidationExceptionContainingMessage("age: expected type: Number, found: String"));

        testServer
                .perform(
                        post("/testRest?schemaFile=schema.json")
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstName\": \"Peter\", \"lastName\": \"Griffin\", \"age\": \"42\"}")
                );
    }

    @Test
    public void shouldFailValidationForMissingRequiredField() throws Exception {
        exception.expect(nestedValidationExceptionContainingMessage("required key [lastName] not found"));

        testServer
                .perform(
                        post("/testRest?schemaFile=schema.json")
                                .contentType(APPLICATION_JSON)
                                .content("{\"firstName\": \"Peter\"}")
                );
    }

    private static Matcher<Throwable> nestedValidationExceptionContainingMessage(String message) {
        return new NestedExceptionTypeAndMessageMatcher(DetailedValidationException.class, message);
    }

    private static class NestedExceptionTypeAndMessageMatcher extends TypeSafeMatcher<Throwable> {

        private final Class<? extends Throwable> expectedType;
        private final String expectedMessage;

        private NestedExceptionTypeAndMessageMatcher(Class<? extends Throwable> expectedType, String expectedMessage) {
            this.expectedType = expectedType;
            this.expectedMessage = expectedMessage;
        }

        @Override
        protected boolean matchesSafely(Throwable item) {
            if (!NestedServletException.class.isAssignableFrom(item.getClass())) return false;
            final Throwable cause = item.getCause();
            return expectedType.isAssignableFrom(cause.getClass()) && cause.getMessage().contains(expectedMessage);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(String
                    .format("NestedServletException with a cause of type %s and message [%s]", expectedType
                            .getName(), expectedMessage));
        }
    }

}