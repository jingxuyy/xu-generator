package com.xu.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * @author xu
 * 静态文件生成器
 */
public class StaticGenerator {
    public static void main(String[] args) {
        // 获取当前项目的根路径
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);

        // 输入路径 File.separator 路径分隔符，根据不同的操作系统，自动选择分隔符
        String inputPath = projectPath+File.separator+"xu-generator-demo-projects"+ File.separator+"acm-template";

        // 输出路径
        String outputPath = projectPath;

        copyFileByHutool(inputPath,outputPath);
    }

    /**
     * 拷贝文件，借助Hutool实现，将输入目录完整拷贝到输出目录
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     */
    public static void copyFileByHutool(String inputPath, String outputPath){
        FileUtil.copy(inputPath, outputPath, false);
    }
}
