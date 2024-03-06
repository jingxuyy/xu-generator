package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;



/**
 * 动静结合生成
 */
public class MainGenerator {

    public static void doGenerate(Object model) throws Exception {
        // 1. 静态文件
        // 获取目标项目的根路径
        String inputRootPath = "${fileConfig.inputRootPath}";
        // 输出路径
        String outputRootPath = System.getProperty("user.dir")+File.separator+"${fileConfig.outputRootPath}";
        System.out.println("outputPath: "+outputRootPath);

        String inputPath;
        String outputPath;

    <#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "static">
        StaticGenerator.copyFileByHutool(inputPath,outputPath);

        <#else>
        DynamicGenerator.doGenerate(inputPath,outputPath,model);

        </#if>
    </#list>

    }
}