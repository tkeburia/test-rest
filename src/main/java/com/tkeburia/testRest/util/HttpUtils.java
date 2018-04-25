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

import com.tkeburia.testRest.dto.HttpRequestAndOtherArgs;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.list;
import static java.util.stream.Collectors.toMap;

public final class HttpUtils {
    private static final String AUTHORIZATION_HEADER_NAME = "authorization";

    private HttpUtils() {

    }

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
        final Object[] args = point.getArgs();
        result.setOtherArgs(stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .collect(Collectors.toList()));

        result.setHttpServletRequest(stream(args)
                .filter(HttpServletRequest.class::isInstance)
                .map(arg -> (HttpServletRequest) arg)
                .findFirst()
                .orElseGet(() -> ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()));
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
