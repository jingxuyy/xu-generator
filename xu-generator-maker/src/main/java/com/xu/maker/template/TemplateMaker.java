package com.xu.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xu.maker.meta.Meta;
import com.xu.maker.meta.enums.FileGenerateTypeEnum;
import com.xu.maker.meta.enums.FileTypeEnum;
import com.xu.maker.template.enums.FileFilterRangeEnum;
import com.xu.maker.template.enums.FileFilterRuleEnum;
import com.xu.maker.template.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 */
public class TemplateMaker {

    /**
     * 制作模板
     * @param templateMakerConfig templateMakerConfig
     * @return id
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
     Long id = templateMakerConfig.getId();
     Meta meta = templateMakerConfig.getMeta();
     String originalProjectPath = templateMakerConfig.getOriginalProjectPath();
     TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
     TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
     TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();

        return makeTemplate(meta, originalProjectPath, templateMakerFileConfig, templateMakerModelConfig, outputConfig, id);
    }

    /**
     * 制作.ftl模板
     *
     * @param meta 目标项目的基本配置信息对象
     * @param originalProjectPath 要制作模板的项目所在的根路径
     * @param templateMakerFileConfig 指定生成.ftl模板的文件列表
     * @param templateMakerModelConfig 配置对象的模型信息
     * @param templateMakerOutputConfig 输出分组文件和分组外文件去重
     * @param id 当前制作的项目id
     * @return id 当前制作的项目id
     */
    public static long makeTemplate(Meta meta, String originalProjectPath, TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig, Long id){
        // 没有id 生成id
        if(id == null){
            id = IdUtil.getSnowflake().nextId();
        }
        // 业务逻辑
        // 避免生成的文件和原始文件在一起，则使用隔离，也就是创建一个工作空间(文件夹，里面存放原始项目的复制版本)
        // 2、输入文件信息
        String projectPath = System.getProperty("user.dir");

        // 复制，复制的时候保证多次复制同一份，都会生成不同的复制版本，互相隔离
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originalProjectPath, templatePath, true);
        }

        // 2、输入文件信息
        // 要挖坑的项目根路径
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null).stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        String metaOutputPath = templatePath + File.separator + "meta.json";


        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        if(FileUtil.exist(metaOutputPath)){
            // 同理meta.json已存在，说明不是第一次制作
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(meta, oldMeta, CopyOptions.create().ignoreNullValue());
            meta = oldMeta;
            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = meta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = meta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // 去重
            meta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            meta.getModelConfig().setModels(distinctModels(modelInfoList));

        }else {
            // meta.json不存在，第一次制作
            // 文件配置
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            // 模型配置
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();


            meta.setFileConfig(fileConfig);

            fileConfig.setSourceRootPath(sourceRootPath);

            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);

            fileInfoList.addAll(newFileInfoList);


            meta.setModelConfig(modelConfig);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);

            modelInfoList.addAll(newModelInfoList);
        }

        if(templateMakerOutputConfig != null){
            // 文件外层和分组去重
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfig.FileInfo> fileInfoList = meta.getFileConfig().getFiles();
                meta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
        }

        // 2. 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), metaOutputPath);

        return id;
    }

    /**
     * 获取模型配置列表
     * @param templateMakerModelConfig templateMakerModelConfig
     * @return List
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        // 本次新增的模型列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        if(templateMakerModelConfig == null){
            return newModelInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if(CollUtil.isEmpty(models)){
            return newModelInfoList;
        }

        // 转换为配置文件接受的modelInfo对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());


        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig !=null){

            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);

            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);

        }else {
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 生成多个文件
     * @param templateMakerFileConfig templateMakerFileConfig
     * @param templateMakerModelConfig templateMakerModelConfig
     * @param sourceRootPath sourceRootPath
     * @return List
     */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        if(templateMakerFileConfig == null){
            return newFileInfoList;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileInfoConfigList)){
            return newFileInfoList;
        }

        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {

            String fileInputPath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + fileInputPath;

            // 传入绝对路径
            // 得到过滤后的文件列表
            List<File> fileList = FileFilter.doFilters(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());
            // 不处理已经生成的ftl模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());

            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, templateMakerModelConfig, sourceRootPath, fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }

        }

        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig !=null){
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);

            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);

        }
        return newFileInfoList;
    }

    /**
     * 制作模板文件
     * @param fileInput fileInput
     * @param templateMakerModelConfig templateMakerModelConfig
     * @param sourceRootPath sourceRootPath
     * @return Meta.FileConfig.FileInfo
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File fileInput, TemplateMakerModelConfig templateMakerModelConfig,
                                                             String sourceRootPath,
                                                             TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        // 这里fileInput是文件，因为外层通过循环遍历，得到的都是绝对路径，封装成文件，方便外层调用
        // 而当前方法，要写入meta.json配置文件需要相对路径，因此使用前需要转换一下

        // 要输出模板文件
        String fileInputAbsolutePath = fileInput.getAbsolutePath();
        fileInputAbsolutePath =fileInputAbsolutePath.replaceAll("\\\\", "/");

        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath+"/", "");

        String fileOutputPath = fileInputPath+".ftl";

        // 二、使用字符串替换，生成模板文件
        // 原始文件的绝对路径

        // 生成的.ftl模板文件的绝对路径
        String fileOutputAbsolutePath = fileInputAbsolutePath+".ftl";

        String fileContent;
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if(hasTemplateFile){
            // 如果.ftl模板文件已经存在，说明当前不是第一次制作，因此本次只需要在原有.ftl模板文件基础上进行替换即可
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else {
            // 不存在，说明第一次制作，需要根据原始文件生成进行替换后，再生成.ftl模板文件
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对于同一个文件内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String replacement;
        String newFileContent = fileContent;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            if(modelGroupConfig == null){
                // 不是分组
                replacement = String.format("${%s}", fieldName);
            }else {
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, fieldName);
            }

            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }



        // 三、生成配置文件

        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());

        // 是否更改了内容
        boolean contentEquals = newFileContent.equals(fileContent);
        // 静态文件判断规则，之前不存在模板文件，并且这次替换的没有修改内容，才是静态文件，缺一不可
        if(!hasTemplateFile){
            // 没有模板
            if(contentEquals){
                // 和原文件内容一致，没有替换，静态生成
                // 输出路径=输入路径
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {
                // 没有模板，但是发生了替换，动态生成
                // 输出模板文件
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        }else if(!contentEquals){
            // 有模板, 且更改了内容
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }

    public static void main(String[] args) {
        // 1、项目基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String projectPath = System.getProperty("user.dir");
        // 要挖坑的项目根路径
        String originalProjectPath = projectPath + File.separator + "xu-generator-demo-projects/springboot-init";

        // 要挖坑的文件
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
        String fileInputPath2 = "src/main/resources/application.yml";

//        List<String> fileInputPathList = new ArrayList<>();
//        fileInputPathList.add(fileInputPath1);
//        fileInputPathList.add(fileInputPath2);
        

        // 输入模型参数信息（第二次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setType("String");

        // 首次
//        String searchStr = "Sum: ";
        // 第二次
//        String searchStr = "BaseResponse";
//
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
//
        // 文件过滤配置
        List<FileFilterConfig> filterConfigList = new ArrayList<>();
        FileFilterConfig filterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        filterConfigList.add(filterConfig);
        fileInfoConfig1.setFilterConfigList(filterConfigList);
//
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(fileInputPath2);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1, fileInfoConfig2);

        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);
//
//        // 文件分组配置
//        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
//        fileGroupConfig.setCondition("outputText");
//        fileGroupConfig.setGroupKey("test");
//        fileGroupConfig.setGroupName("测试分组");
//        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        // 模型组配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMakerOutputConfig templateMakerOutputConfig = new TemplateMakerOutputConfig();


        long id = TemplateMaker.makeTemplate(meta, originalProjectPath, templateMakerFileConfig, templateMakerModelConfig, templateMakerOutputConfig, 1768500939746566144L);
        System.out.println(id);
    }

    /**
     * 文件去重
     *
     * @param fileInfoList fileInfoList
     * @return newFileInfoList
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        // 1. 将所有文件配置分为有分组和无分组

        // 先处理有分组的文件
        // 以组为单位划分
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // 同组内配置合并
        // 定义存放合并后的map
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();

        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的group配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }
        // 2. 对有分组的文件配置，如果有相同的分组，同分组内的文件进行合并，不同分组可同时保留
        // 3. 创建新的文件配置列表， 先将合并后的分组添加到结果列表
        ArrayList<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());
        // 4. 再将无分组的文件配置列表添加到结果列表

        resultList.addAll(new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r)->r)
                ).values()));
        return resultList;
    }


    /**
     * 模型去重
     *
     * @param modelInfoList modelInfoList
     * @return newModelInfoList
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        // 1. 将所有文件配置分为有分组和无分组

        // 先处理有分组的文件
        // 以组为单位划分
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        // 同组内配置合并
        // 定义存放合并后的map
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();

        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                    ).values());

            // 使用新的group配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }
        // 2. 对有分组的文件配置，如果有相同的分组，同分组内的文件进行合并，不同分组可同时保留
        // 3. 创建新的文件配置列表， 先将合并后的分组添加到结果列表
        ArrayList<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());
        // 4. 再将无分组的文件配置列表添加到结果列表

        resultList.addAll(new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r)->r)
                ).values()));
        return resultList;
    }



}
