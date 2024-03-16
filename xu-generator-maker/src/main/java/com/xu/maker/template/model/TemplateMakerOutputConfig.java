package com.xu.maker.template.model;

import lombok.Data;

/**
 * @author: xuJing
 * @date: 2024/3/15 18:58
 */

@Data
public class TemplateMakerOutputConfig {

    // 是否移除，配置文件里根目录存在的文件(根目录里存在的文件与组内文件同名)
    private boolean removeGroupFilesFromRoot = true;

}
