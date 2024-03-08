# ${name}

> ${description}
>
> 作者：${author}
>
> 基于 [程序员xu](https://github.com/jingxuyy) 的 [代码生成器项目](https://github.com/jingxuyy/xu-generator) 制作，感谢您的使用！

 可以通过命令行交互输入的方式动态生成想要的项目代码

## 使用说明

 执行项目根目录的脚本文件：

```
generator <命令> <选项参数>
```

命令示例：
```
generator generate <#list modelConfig.models as modelInfo> <#if modelInfo.groupKey??><#else><#if modelInfo.abbr??>-${modelInfo.abbr}<#else>-${modelInfo.fieldName}</#if></#if> </#list>
```

## 参数说明

<#list modelConfig.models as modelInfo>
  <#if modelInfo.groupKey??>
  <#else>
  - ${modelInfo?index + 1}）${modelInfo.fieldName}

  > 类型：${modelInfo.type}
  >
  > 描述：${modelInfo.description}
  >
  > 默认值：${modelInfo.defaultValue?c}
  >
  > 缩写：<#if modelInfo.abbr??>-${modelInfo.abbr}</#if>
  </#if>
</#list>