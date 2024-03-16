package ${basePackage}.cli.command;


import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;

import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;


<#macro generateOpion indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent}* ${modelInfo.description}
${indent}*/
</#if>
${indent}@Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}",</#if> "--${modelInfo.fieldName}"}, <#if modelInfo.description??>description = "${modelInfo.description}",</#if> arity = "0..1", interactive = true, echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??>=${modelInfo.defaultValue?c}</#if>;

</#macro>

<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置");
${indent}CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}CommandLine.execute(${modelInfo.allArgsStr});
</#macro>


@Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {
    <#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    <#if modelInfo.groupName??>
    /**
     * ${modelInfo.groupName}
     */
    </#if>
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    @Command(name = "${modelInfo.groupKey}", description = "${modelInfo.description}")
    @Data
    public static class ${modelInfo.type}Command implements Runnable {
        <#list modelInfo.models as modelInfo>
            <@generateOpion indent="        " modelInfo=modelInfo/>
        </#list>

        @Override
        public void run(){
        <#list  modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
        </#list>
        }
    }
        <#else>
            <@generateOpion indent="    " modelInfo=modelInfo/>
        </#if>
    </#list>


    @Override
    public Integer call() throws Exception {
    <#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    <#if modelInfo.condition??>
      if(${modelInfo.condition}){
        <@generateCommand indent="          " modelInfo=modelInfo/>
      }
    <#else>
    <@generateCommand indent="      " modelInfo=modelInfo/>
    </#if>
    </#if>
    </#list>
      DataModel dataModel = new DataModel();
<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
      dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
    </#if>
</#list>
      BeanUtil.copyProperties(this, dataModel);
      MainGenerator.doGenerate(dataModel);
      return 0;
    }
}
