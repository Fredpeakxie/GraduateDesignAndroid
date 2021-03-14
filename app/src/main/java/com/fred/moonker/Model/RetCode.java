package com.fred.moonker.Model;

public enum RetCode {
    OK(200),
    WRONG_PASSWORD(400),
    REPETITIVE_USERNAME(410),
    REPETITIVE_EMAIL(411),
    REPETITIVE_USERNAME_EMAIL(412),
    ;

    private final Integer code;

    RetCode(Integer code) {
        this.code = code;
    }
}
