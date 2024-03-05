package com.xu.maker.generator;

import cn.hutool.core.io.FileUtil;
import com.xu.maker.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态文件生成器
 */
public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir")+File.separator+"xu-generator-basic";
        String inputPath = projectPath+File.separator+"src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath+File.separator+"MainTemplate.java";

        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("徐京");
        mainTemplateConfig.setOutputText("本次计算结果为");
        mainTemplateConfig.setLoop(false);

        doGenerate(inputPath,outputPath,mainTemplateConfig);
    }


    /**
     * 生成文件
     * @param inputPath 模板输入路径
     * @param outputPath 输出路径
     * @param model 数据模型
     * inputPath是模板路径+模板文件名
     *              根据inputFile.getParentFile()获得当前模板的父路径，也就是模板所存在的直接文件夹
     *              根据inputFile.getName()获取当前模板的名称，用来指定加载哪一个模板
     * outputPath 是输出路径+输出的文件名
     */
    public static void doGenerate(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 创建出对应FreeMarker版本号的Configuration对象
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File inputFile = new File(inputPath);

        File templateDir = inputFile.getParentFile();

        // 配置模板文件所在的路径
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 默认数字中间每三位加个逗号，此设置取消这个默认
        configuration.setNumberFormat("0.######");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate(inputFile.getName());


        // 输出文件位置和文件名
        Writer writer = new FileWriter(outputPath);

        // 生成文件
        template.process(model, writer);

        // 关闭流
        writer.close();
    }
}
