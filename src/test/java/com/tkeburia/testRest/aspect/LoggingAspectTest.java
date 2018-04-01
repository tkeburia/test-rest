package com.tkeburia.testRest.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.org.lidalia.slf4jext.Level.INFO;

@RunWith(MockitoJUnitRunner.class)
public class LoggingAspectTest {

    private static final String MESSAGE_CONTENT = "message content";
    private static final String DESTINATION = "queue1";
    private static final String METHOD_SIGNATURE = "method.signature";
    private static final String X_TEST_HEADER = "x-test-header";
    private static final String TEST_HEADER_VALUE = "test-value";
    private static final String PARAM_KEY = "paramKey";
    private static final String PARAM_VALUE = "paramValue";
    private static final String RESPONSE_KEY = "response";
    private static final String RESPONSE_VALUE = "ok";

    private TestLogger TLOG = TestLoggerFactory.getTestLogger(LoggingAspect.class);

    private LoggingAspect loggingAspect;

    private Map<String, String> params;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Message message;

    @Mock
    private Destination destination;

    @Before
    public void setup() {
        loggingAspect = new LoggingAspect(new ObjectMapper(), singletonList("Content-type"));
        params = ImmutableMap.of(PARAM_KEY, PARAM_VALUE);
        TLOG.clearAll();
    }

    @Test
    public void shouldLogInfoForControllerMethod() throws JsonProcessingException {
        TLOG.setEnabledLevels(INFO);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(METHOD_SIGNATURE);
        when(joinPoint.getArgs()).thenReturn(new Object[]{request, params});
        when(request.getHeaderNames()).thenReturn(enumeration(asList("Content-type", "Accept")));
        when(request.getHeader(ACCEPT)).thenReturn(APPLICATION_JSON_VALUE);
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(X_TEST_HEADER, singletonList(TEST_HEADER_VALUE));
        ResponseEntity<?> result = new ResponseEntity<Object>(ImmutableMap
                .of(RESPONSE_KEY, RESPONSE_VALUE), httpHeaders, OK);
        loggingAspect.logControllerMethod(joinPoint, result);
        assertEquals(1, TLOG.getAllLoggingEvents().size());
        assertEquals("{\"method\":[\"" + METHOD_SIGNATURE + "\"]," +
                        "\"params\":[{\"" + PARAM_KEY + "\":\"" + PARAM_VALUE + "\"}]," +
                        "\"result\":[\"<200 OK,{" + RESPONSE_KEY + "=" + RESPONSE_VALUE + "}," +
                        "{" + X_TEST_HEADER + "=[" + TEST_HEADER_VALUE + "]}>\"]," +
                        "\"headers\":{\"" + ACCEPT + "\":\"" + APPLICATION_JSON_VALUE + "\"}}",
                TLOG.getAllLoggingEvents().get(0).getMessage());
        // suppressed header should not be pulled from the request
        verify(request, never()).getHeader("Content-type");
    }

    @Test
    public void shouldLogMethodData() throws JMSException, JsonProcessingException {
        TLOG.setEnabledLevels(INFO);
        when(message.getJMSDestination()).thenReturn(destination);
        when(destination.toString()).thenReturn(DESTINATION);
        when(message.toString()).thenReturn(MESSAGE_CONTENT);

        loggingAspect.logMethodData(message);

        assertEquals(2, TLOG.getAllLoggingEvents().size());
        assertEquals("Received message on queue " + DESTINATION, TLOG.getAllLoggingEvents().get(0).getMessage());
        assertEquals("Queue Message content : " + MESSAGE_CONTENT, TLOG.getAllLoggingEvents().get(1).getMessage());
    }
}