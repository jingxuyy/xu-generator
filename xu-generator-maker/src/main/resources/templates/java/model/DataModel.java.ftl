package ${basePackage}.model;

import lombok.Data;

<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent}* ${modelInfo.description}
${indent}*/
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

/**
 * 数据模型
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    // 是组,创建静态内部类
    <#if modelInfo.description??>
    /**
    * ${modelInfo.description}
    */
    </#if>
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    @Data
    public static class ${modelInfo.type} {
        // 遍历组，获取属性
        <#list modelInfo.models as modelInfo>
            <@generateModel indent="        " modelInfo=modelInfo/>
        </#list>
    }

    <#else>
    // 不是组,创建成员量
        <@generateModel indent="    " modelInfo=modelInfo/>

    </#if>
</#list>

}