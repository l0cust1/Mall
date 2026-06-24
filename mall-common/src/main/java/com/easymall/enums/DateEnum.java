package com.easymall.enums;

public enum DateEnum {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), 
    YYYY_MM_DD("yyyy-MM-dd"), 
    YYYYMM("yyyyMM");
    private String pattern;
    DateEnum(String pattern) {
        this.pattern = pattern;
    }
    public String getPattern() {
        return pattern;
    }
}
