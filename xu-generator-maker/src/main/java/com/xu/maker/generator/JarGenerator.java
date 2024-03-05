package com.xu.maker.generator;

import java.io.*;

public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        // 调用 Process 类执行 Maven 打包命令
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand;

        String os = System.getProperty("os.name");
        if(os !=null && os.toLowerCase().startsWith("windows")){
            mavenCommand = winMavenCommand;
        }else {
            mavenCommand = otherMavenCommand;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        // 输出打包信息
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line=bufferedReader.readLine()) != null){
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("命令执行结束，退出码："+exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("D:\\code\\xu-generator\\xu-generator-basic");
    }
}
