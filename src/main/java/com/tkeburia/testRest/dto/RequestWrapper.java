package com.tkeburia.testRest.dto;

import com.tkeburia.testRest.annotation.CustomConstraint;
import lombok.Data;

import java.io.Serializable;

@Data
public class RequestWrapper implements Serializable
{
    @CustomConstraint
    EnumActionValues enumActionValues;
}
