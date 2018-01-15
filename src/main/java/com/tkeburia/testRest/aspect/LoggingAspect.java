package com.tkeburia.testRest.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.tkeburia.testRest.dto.HttpRequestAndOtherArgs;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("#{'${suppressed.headers}'.split(',')}")
    private List<String> exceptHeaders;

    @Autowired
    public LoggingAspect(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @AfterReturning(
            pointcut = "@annotation(org.springframework.web.bind.annotation.RequestMapping)",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint point, Object result) throws JsonProcessingException
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


}
