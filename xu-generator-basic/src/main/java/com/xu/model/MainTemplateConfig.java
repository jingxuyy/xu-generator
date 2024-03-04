package com.xu.model;

import lombok.Data;

/**
 * 模板配置
 */
@Data
public class MainTemplateConfig {

    // 动态生成需求
    // 1. 在代码开头增加作者 @Author 注释 （增加代码）
    // 2. 修改程序输出的信息提示 （替换代码）
    // 3. 将循环读取改为单次读取（可选代码）

    /**
     * 作者 （字符串填充值）
     */
    private String author="author";

    /**
     * 输出信息
     */
    private String outputText="outputText";

    /**
     * 是否循环（开关）
     */
    private boolean loop=false;

}
