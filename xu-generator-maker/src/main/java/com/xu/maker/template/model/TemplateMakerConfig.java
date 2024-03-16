package com.xu.maker.template.model;

import com.xu.maker.meta.Meta;
import lombok.Data;

/**
 * 模板制作配置
 * @author: xuJing
 * @date: 2024/3/15 14:41
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originalProjectPath;

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
