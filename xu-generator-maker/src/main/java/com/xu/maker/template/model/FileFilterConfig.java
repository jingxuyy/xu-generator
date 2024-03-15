package com.xu.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤配置
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 过滤范围(文件名，文件内容)
     */
    private String range;

    /**
     * 过滤规则（包含、相等、正则、前缀、后缀）
     */
    private String rule;

    /**
     * 过滤值
     */
    private String value;
}
