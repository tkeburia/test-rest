package com.tkeburia.testRest.util;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
        "suppressed.headers=x-header-a,x-header-b",
})
public class HttpUtilsTest {

    @Value("#{'${suppressed.headers}'.split(',')}")
    private List<String> exceptHeaders;

    @Mock
    private HttpServletRequest request;

    @Test
    public void shouldReturnHeadersExceptSuppressedHeadersAndDecodeBasicAuth() {
        Map<String, String> headers = ImmutableMap.of(
                "x-header-a", "value-1",
                "x-header-b", "value-2",
                "x-header-c", "value-3",
                "authorization", "Basic dXNlcjpwYXNzd29yZA=="
        );
        final Enumeration<String> enumeration = Collections.enumeration(headers.keySet());

        when(request.getHeaderNames()).thenReturn(enumeration);
        when(request.getHeader("x-header-c")).thenReturn("value-3");
        when(request.getHeader("authorization")).thenReturn("Basic dXNlcjpwYXNzd29yZA==");

        final Map<String, String> headerMap = HttpUtils.getHeaderMap(request, exceptHeaders);

        assertEquals(2, headerMap.keySet().size());
        assertEquals("value-3", headerMap.get("x-header-c"));
        assertEquals("Basic dXNlcjpwYXNzd29yZA== (user:password)", headerMap.get("authorization"));
    }

}