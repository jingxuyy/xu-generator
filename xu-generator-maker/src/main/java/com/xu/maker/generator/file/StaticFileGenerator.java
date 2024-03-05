package com.xu.maker.generator.file;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * @author xu
 * 静态文件生成器
 */
public class StaticFileGenerator {

    /**
     * 拷贝文件，借助Hutool实现，将输入目录完整拷贝到输出目录
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     */
    public static void copyFileByHutool(String inputPath, String outputPath){
        FileUtil.copy(inputPath, outputPath, false);
    }
}
