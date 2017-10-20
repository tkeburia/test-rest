package com.tk.testRest.util;

import com.tk.testRest.dto.HttpRequestAndOtherArgs;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Collections.list;
import static java.util.stream.Collectors.toMap;

public class Utils {
    private static final String AUTHORIZATION_HEADER_NAME = "authorization";

    public static Map<String, String> getHeaderMap(HttpServletRequest request, List<String> suppressedHeaders) {
        if (request == null) return null;

        final Map<String, String> headerMap = list(request.getHeaderNames())
                .stream()
                .filter(h -> !suppressedHeaders.contains(h))
                .collect(toMap(k -> k, request::getHeader));

        return decodeBasicAuth(headerMap);
    }

    public static HttpRequestAndOtherArgs separateHttpRequestArgsFromOthers(JoinPoint point) {
        final HttpRequestAndOtherArgs result = new HttpRequestAndOtherArgs();
        result.setOtherArgs(stream(point.getArgs())
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .collect(Collectors.toList()));

        result.setHttpServletRequest(stream(point.getArgs())
                .filter(HttpServletRequest.class::isInstance)
                .map(arg -> (HttpServletRequest) arg)
                .findFirst()
                .orElse(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
        return result;

    }

    private static Map<String, String> decodeBasicAuth(Map<String, String> headerMap) {
        final Map<String, String> result = headerMap;
        if (!result.containsKey(AUTHORIZATION_HEADER_NAME)
                || !result.get(AUTHORIZATION_HEADER_NAME).startsWith("Basic ")) { return result; }
        final String authHeader = result.get(AUTHORIZATION_HEADER_NAME);
        final String s = authHeader.split(" ")[1];
        final String decoded = new String(Base64.getDecoder().decode(s));
        result.put(AUTHORIZATION_HEADER_NAME, String.format("%s (%s)", authHeader, decoded));
        return result;
    }
}
