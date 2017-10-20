package com.tk.testRest.dto;

import com.tk.testRest.annotation.CustomConstraint;
import lombok.Data;

import java.io.Serializable;

@Data
public class RequestWrapper implements Serializable
{
    @CustomConstraint
    EnumActionValues enumActionValues;
}
