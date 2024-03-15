package com.xu.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.xu.maker.meta.Meta;
import com.xu.maker.template.enums.FileFilterRangeEnum;
import com.xu.maker.template.enums.FileFilterRuleEnum;
import com.xu.maker.template.model.FileFilterConfig;
import com.xu.maker.template.model.TemplateMakerConfig;
import com.xu.maker.template.model.TemplateMakerFileConfig;
import com.xu.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateMakerTest {

    @Test
    public  void testMakeTemplateBug1() {
        // 1、项目基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String projectPath = new File(System.getProperty("user.dir")).getParent();
        // 要挖坑的项目根路径
        String originalProjectPath = projectPath + File.separator + "xu-generator-demo-projects/springboot-init";
        // 要挖坑的文件
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";


        // 模型组配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFileName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);


        // 文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        long id = TemplateMaker.makeTemplate(meta, originalProjectPath, templateMakerFileConfig, templateMakerModelConfig, 1L);
        System.out.println(id);
    }

    @Test
    public  void testMakeTemplateBug2() {
        // 1、项目基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String projectPath = new File(System.getProperty("user.dir")).getParent();
        // 要挖坑的项目根路径
        String originalProjectPath = projectPath + File.separator + "xu-generator-demo-projects/springboot-init";
        // 要挖坑的文件
        String fileInputPath1 = "./";


        // 模型组配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFileName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);


        // 文件过滤配置
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1));

        long id = TemplateMaker.makeTemplate(meta, originalProjectPath, templateMakerFileConfig, templateMakerModelConfig, 2L);
        System.out.println(id);
    }


    @Test
    public void testMakeTemplateWithJSON(){
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

}