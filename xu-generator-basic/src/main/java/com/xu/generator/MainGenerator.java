package com.xu.generator;

import com.xu.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

import static com.xu.generator.DynamicGenerator.doGenerate;
import static com.xu.generator.StaticGenerator.copyFileByHutool;

/**
 * 动静结合生成
 */
public class MainGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        // 1. 静态文件
        // 获取当前项目的根路径
        String projectPath = System.getProperty("user.dir");

        // 输入路径 File.separator 路径分隔符，根据不同的操作系统，自动选择分隔符
        String inputPath = projectPath+ File.separator+"xu-generator-demo-projects"+ File.separator+"acm-template";

        // 输出路径
        String outputPath = projectPath;

        copyFileByHutool(inputPath,outputPath);

        // 2.动态文件
        String dynamicInputPath = projectPath+File.separator+"xu-generator-basic"+File.separator+"src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath+File.separator+"acm-template/src/com/yupi/acm/MainTemplate.java";

        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("徐");
        mainTemplateConfig.setOutputText("本次计算结果为：");
        mainTemplateConfig.setLoop(true);

        doGenerate(dynamicInputPath,dynamicOutputPath,mainTemplateConfig);
    }
}
