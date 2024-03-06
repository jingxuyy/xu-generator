package com.xu.maker.meta.enums;

/**
 * 文件生成类型枚举
 */
public enum FileGenerateTypeEnum {

    /**
     * 动态
     */
    DYNAMIC("动态", "dynamic"),

    /**
     * 静态
     */
    STATIC("静态", "static");





    private final String text;

    private final String value;

    FileGenerateTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
