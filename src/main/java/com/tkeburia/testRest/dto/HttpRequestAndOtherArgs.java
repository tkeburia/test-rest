package com.tkeburia.testRest.dto;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Data
public class HttpRequestAndOtherArgs
{
    private HttpServletRequest httpServletRequest;
    private List<Object> otherArgs;
}
