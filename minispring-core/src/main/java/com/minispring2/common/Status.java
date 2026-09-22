package com.minispring2.common;

/**
 * @description
 * @create 2026-09-22 10:53:34
 **/
public enum Status {
    SUCCESS("1"),
    FAIL("0");

    private final String code;

    Status(String code) {   // 默认就是 private，别写 public
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
