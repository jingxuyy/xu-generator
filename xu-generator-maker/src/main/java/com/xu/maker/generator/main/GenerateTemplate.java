package com.xu.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.xu.maker.generator.JarGenerator;
import com.xu.maker.generator.ScriptGenerator;
import com.xu.maker.generator.file.DynamicFileGenerator;
import com.xu.maker.meta.Meta;
import com.xu.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;


public abstract class GenerateTemplate {

    public void doGenerate() throws Exception{

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

        // 从原始模板文件路径复制到生成的代码包中
        String sourceCopyDestPath = copySource(meta, outputPath);
        generateCode(meta, outputPath);

        // 构建jar包
        String jarPath = buildJar(meta, outputPath);

        // 封装脚本
        String shellOutputFilePath = buildScript(jarPath, outputPath);

        // 生成精简版程序
        buildDist(outputPath, sourceCopyDestPath, shellOutputFilePath, jarPath);

    }

    protected String buildScript(String  jarPath, String outputPath){
        String shellOutputFilePath = outputPath+File.separator+"generator";
        ScriptGenerator.doGenerate(shellOutputFilePath, jarPath);
        return shellOutputFilePath;
    }

    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        String distOutputPath = outputPath + "-dist";
        // - 拷贝jar包
        String targetAbsolutePath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);

        String jarAbsolutePath = jarPath;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // - 拷贝脚本文件
        FileUtil.copy(shellOutputFilePath, distOutputPath, true);
        FileUtil.copy(shellOutputFilePath + ".bat", distOutputPath, true);
        // - 拷贝源模板文件
        FileUtil.copy(sourceCopyDestPath, distOutputPath, true);
    }


    protected String buildJar(Meta meta, String outputPath) throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = outputPath+File.separator+"target/"+jarName;
        return jarPath;
    }

    protected void generateCode(Meta meta, String outputPath) throws IOException, TemplateException {
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
        String outputBaseJavaPackagePath = outputPath+ File.separator+"src/main/java/"+outputBasePackagePath;

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

        //README
        inputFilePath = inputResourcePath+File.separator+"templates\\README.md.ftl";
        outputFilePath = outputPath+File.separator+"/README.md";
        DynamicFileGenerator.doGenerate(inputFilePath,outputFilePath, meta);
    }

    protected String copySource(Meta meta, String outputPath) {
        /**
         * 作用：复制要生成代码文件的模板文件
         * 含义：如果不生成，则此程序生成的代码生成器，如果要传递给别人，则还要把模板文件发送，因此，当前这一步，是把要生成的代码
         *      模板也复制到最后生成代码生成器的项目里，这样jar可以直接运行
         */
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
        return sourceCopyDestPath;
    }
}
