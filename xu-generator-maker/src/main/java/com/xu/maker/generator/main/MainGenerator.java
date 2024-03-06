package com.xu.maker.generator.main;


public class MainGenerator extends GenerateTemplate{

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        System.out.println("不输出精简版");
    }

    public static void main(String[] args) throws Exception {

        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}
