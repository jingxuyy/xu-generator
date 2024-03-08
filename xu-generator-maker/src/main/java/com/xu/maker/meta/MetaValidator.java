package com.xu.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.xu.maker.meta.enums.FileGenerateTypeEnum;
import com.xu.maker.meta.enums.FileTypeEnum;
import com.xu.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class MetaValidator {

    public static void doValidAndFill(Meta meta){
        // 基础信息校验和默认值
        validAndFillMetaRoot(meta);

        // fileConfig 校验和默认值
        validAndFillFileConfig(meta);

        // modelConfig 校验和默认值
        validAndFillModelConfig(meta);

    }

    private static void validAndFillModelConfig(Meta meta) {
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            throw new MetaException("未填写 modelConfig");
        }

        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            throw new MetaException("未填写 models");
        }

        for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
            if(StrUtil.isNotEmpty(modelInfo.getGroupKey())){
                List<Meta.ModelConfig.ModelInfo> subModelInfoList = modelInfo.getModels();
                String allArgsStr = subModelInfoList.stream()
                        .map(subModelInfo -> String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgsStr);
                continue;
            }

            String fieldName = modelInfo.getFieldName();
            if(StrUtil.isBlank(fieldName)){
                throw new MetaException("未填写 fieldName");
            }

            String type = StrUtil.emptyToDefault(modelInfo.getType(), ModelTypeEnum.STRING.getValue());
            modelInfo.setType(type);
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            throw new MetaException("未填写fileConfig值");
        }

        // sourceRootPath 必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        if(StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("未填写sourceRootPath值");
        }

        // 源模板文件复制的目的地址，无则使用 .source + sourceRootPath路径的最后一个文件夹拼接
        String inputRootPath = StrUtil.emptyToDefault(fileConfig.getInputRootPath(), ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString());
        // 代码生成器输出路径，无则为generated
        String outputRootPath = StrUtil.emptyToDefault(fileConfig.getOutputRootPath(), "generated");
        String fileConfigType = StrUtil.emptyToDefault(fileConfig.getType(), FileTypeEnum.DIR.getValue());

        fileConfig.setInputRootPath(inputRootPath);
        fileConfig.setOutputRootPath(outputRootPath);
        fileConfig.setType(fileConfigType);

        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        if (CollUtil.isEmpty(files)) {
            throw new MetaException("未填写files值");
        }
        for (Meta.FileConfig.FileInfo fileInfo : files) {
            if(FileTypeEnum.GROUP.getValue().equals(fileInfo.getType())){
                continue;
            }
            // inputPath 必填
            String inputPath = fileInfo.getInputPath();
            if(StrUtil.isBlank(inputPath)){
                throw new MetaException("未填写 inputPath");
            }

            // 默认等于inputPath
            String outputPath = StrUtil.emptyToDefault(fileInfo.getOutputPath(), inputPath);
            // 默认 inputPath 有文件后缀就是file 否则dir
            String type = StrUtil.blankToDefault(fileInfo.getType(), StrUtil.isBlank(FileUtil.getSuffix(inputPath)) ? FileTypeEnum.DIR.getValue() : FileTypeEnum.FILE.getValue());
            // 文件结尾不是 ftl 为static
            String generateType = StrUtil.blankToDefault(fileInfo.getGenerateType(), inputPath.endsWith(".ftl") ? FileGenerateTypeEnum.DYNAMIC.getValue() : FileGenerateTypeEnum.STATIC.getValue());

            fileInfo.setOutputPath(outputPath);
            fileInfo.setType(type);
            fileInfo.setGenerateType(generateType);
        }

    }

    private static void validAndFillMetaRoot(Meta meta) {
        String name = StrUtil.blankToDefault(meta.getName(), "my-generatpr");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的代码生成器");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.xu");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        String author = StrUtil.blankToDefault(meta.getAuthor(), "xu");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());

        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);
    }
}
