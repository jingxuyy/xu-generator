package com.xu.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.xu.maker.template.enums.FileFilterRangeEnum;
import com.xu.maker.template.enums.FileFilterRuleEnum;
import com.xu.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {


    /**
     * 对多个文件进行过滤
     * @param filePath 文件夹或者文件
     * @param fileFilterConfigList 过滤规则
     * @return 符合过滤的文件列表
     */
    public static List<File> doFilters(String filePath, List<FileFilterConfig> fileFilterConfigList){
        // 根据路径获取所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList, file))
                .collect(Collectors.toList());
    }


    /**
     * 过滤单个文件
     * @param fileFilterConfigList 过滤规则
     * @param file 单个文件
     * @return 是否过滤
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // 所有过滤器校验后的结果
        boolean result = true;

        if(CollUtil.isEmpty(fileFilterConfigList)){
            return true;
        }

        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if(fileFilterRangeEnum == null){
                continue;
            }

            // 要过滤的原内容
            String content;
            switch (fileFilterRangeEnum){
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
                    content = fileName;
                    break;
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if(fileFilterRuleEnum == null){
                continue;
            }
            switch (fileFilterRuleEnum){
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }

            if(!result){
                return false;
            }

        }
        return true;
    }
}
