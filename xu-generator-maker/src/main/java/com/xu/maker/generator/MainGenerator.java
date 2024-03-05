package com.xu.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.xu.maker.generator.file.DynamicFileGenerator;
import com.xu.maker.generator.file.ScriptGenerator;
import com.xu.maker.meta.Meta;
import com.xu.maker.meta.MetaManager;


import java.io.File;


public class MainGenerator {
    public static void main(String[] args) throws Exception {
        Meta meta = MetaManager.getMetaObject();

        // 输出根路径
        /**
         * 输出路径
         * 作用：在当前项目路径里创建一个generated的包
         * 含义：生成的项目的代码都放在这个包里
         * FileUtil.exist(outputPath)：判断这个路径是否存在，不存在创建，主要是解决第一次运行没有generated目录
         */
        String projectPath = System.getProperty("user.dir")+ File.separator+"xu-generator-maker";
        String outputPath = projectPath+File.separator+"generated" + File.separator + meta.getName();
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }

        // 读取模板路径
        /**
         * 读取模板路径
         * 作用：当前要创建的数据模型类，放在resources目录里，获取绝对路径
         * 含义：用于后面生成数据模型时，寻找模板提供父路径
         */
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // 构建生成项目的Java包路径
        /**
         * 构建生成项目的Java包路径
         * 作用：根据读取的配置文件对象里的basePackage属性，创建要生成的项目的父包
         * 含义：通过配置文件对象里的name属性，得知项目名，创建项目总包名，再根据basePackage属性创建项目二级目录
         */
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));

        // 完整路径拼接
        String outputBaseJavaPackagePath = outputPath+File.separator+"src/main/java/"+outputBasePackagePath;

        //model.DataModel
        /**
         * 生成数据模型
         * 作用：根据模板路径，输出路径，在输出路径生成数据模型
         * 含义：根据resources绝对路径，找到数据模型模板，根据项目包路径找到输出位置
         */
        // 动态模板路径
        String inputFilePath;
        // 输出路径
        String outputFilePath;

        // model
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\model\\DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);


        // cli.command.GenerateCommand
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\cli\\command\\GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\cli\\command\\ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\cli\\command\\ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\cli\\CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // java
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //generator.DynamicGenerator
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\generator\\DynamicGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //generator.StaticGenerator
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\generator\\StaticGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //generator.MainGenerator
        inputFilePath = inputResourcePath+File.separator+"templates\\java\\generator\\MainGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath+"/generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        //pom
        inputFilePath = inputResourcePath+File.separator+"templates\\pom.xml.ftl";
        outputFilePath = outputPath+File.separator+"/pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);

        // 构建jar包
        JarGenerator.doGenerate(outputPath);

        // 封装脚本
        String shellOutputFilePath = projectPath+File.separator+"generated/generator";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = outputPath+File.separator+"target/"+jarName;
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);



    }
}
