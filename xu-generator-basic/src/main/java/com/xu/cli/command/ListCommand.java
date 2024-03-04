package com.xu.cli.command;

import cn.hutool.core.io.FileUtil;
import com.xu.cli.example.ASCIIArt;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

@Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{




    @Override
    public void run() {
        // 整个项目根路径
        String projectPath = System.getProperty("user.dir");

        // 输入路径
        String inputPath = new File(projectPath, "xu-generator-demo-projects/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ListCommand()).execute(args);
        System.exit(exitCode);
    }
}
