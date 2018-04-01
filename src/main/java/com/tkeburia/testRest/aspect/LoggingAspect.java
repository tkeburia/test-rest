package com.tkeburia.testRest.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.dto.HttpRequestAndOtherArgs;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.Map;

import static com.tkeburia.testRest.util.HttpUtils.getHeaderMap;
import static com.tkeburia.testRest.util.HttpUtils.separateHttpRequestArgsFromOthers;
import static java.util.Collections.singletonList;

@Component
@Aspect
public class LoggingAspect
{
    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspect.class);

    private final ObjectMapper objectMapper;
    private final List<String> exceptHeaders;

    @Autowired
    public LoggingAspect(ObjectMapper objectMapper, @Value("#{'${suppressed.headers}'.split(',')}") List<String> exceptHeaders) {
        this.objectMapper = objectMapper;
        this.exceptHeaders = exceptHeaders;
    }

    @AfterReturning(
            pointcut = "@annotation(org.springframework.web.bind.annotation.RequestMapping)",
            returning = "result"
    )
    public void logControllerMethod(JoinPoint point, Object result) throws JsonProcessingException
    {
        final HttpRequestAndOtherArgs groupedArgs = separateHttpRequestArgsFromOthers(point);

        Map map = ImmutableMap.of(
                "method", singletonList(point.getSignature().toShortString()),
                "params", groupedArgs.getOtherArgs(),
                "result", singletonList(result.toString()),
                "headers", getHeaderMap(groupedArgs.getHttpServletRequest(), exceptHeaders)
        );

        LOG.info(objectMapper.writeValueAsString(map));
    }


    @Before(
            value = "@annotation(com.tkeburia.testRest.annotation.LogMethodData) && args(message)",
            argNames = "message"

    )
    public void logMethodData(Message message) throws JMSException, JsonProcessingException {
        LOG.info(String.format("Received message on queue %s", message.getJMSDestination().toString()));
        LOG.info(String.format("Queue Message content : %s", message.toString()));
    }

}
