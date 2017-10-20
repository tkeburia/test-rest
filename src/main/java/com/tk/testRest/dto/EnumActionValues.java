package com.tk.testRest.dto;

import java.util.Arrays;
import java.util.List;

public enum EnumActionValues {

    WAIT,
    OFFLINE,
    LOGGED_IN,
    LOGGED_OUT,
    OTHER;

    public List<EnumActionValues> getAllowedActions() {
        return Arrays.asList(
                WAIT,
                OFFLINE,
                OTHER
        );
    }
}