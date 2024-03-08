package ${basePackage}.generator;

import freemarker.template.TemplateException;
import ${basePackage}.model.DataModel;

import java.io.File;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFileByHutool(inputPath,outputPath);
        <#else>
${indent}DynamicGenerator.doGenerate(inputPath,outputPath,model);
        </#if>

</#macro>



/**
 * 动静结合生成
 */
public class MainGenerator {

    public static void doGenerate(DataModel model) throws Exception {
        // 1. 静态文件
        // 获取目标项目的根路径
        String inputRootPath = "${fileConfig.inputRootPath}";
        // 输出路径
        String outputRootPath = System.getProperty("user.dir")+File.separator+"${fileConfig.outputRootPath}";
        System.out.println("outputPath: "+outputRootPath);

        String inputPath;
        String outputPath;

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as modelInfos>
        ${modelInfos.type} ${modelInfos.fieldName} = model.${modelInfo.groupKey}.${modelInfos.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}, 以下文件属于同一组
        // 判断组文件是否存在 ${fileInfo.condition}
        <#if fileInfo.condition??>
        if (${fileInfo.condition}){
            // 存在${fileInfo.condition}，这时候受到${fileInfo.condition}指令判断这组文件是否需要生成
            // 组中的文件在files属性中，遍历组中的文件
        <#list fileInfo.files as fileInfo>
            <@generateFile indent="            " fileInfo=fileInfo/>
        </#list>
        }
        <#else>
        // 组文件不存在 ${fileInfo.condition}，说明组文件不受指令控制，因此正常生成
    <#list fileInfo.files as fileInfo>
        <@generateFile indent="         " fileInfo=fileInfo/>
    </#list>
        </#if>
    <#else>
    <#if fileInfo.condition??>
        if (${fileInfo.condition}){
        <@generateFile indent="            " fileInfo=fileInfo/>
        }
    <#else>
        <@generateFile indent="        " fileInfo=fileInfo/>
    </#if>
    </#if>
</#list>

    }
}