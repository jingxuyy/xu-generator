package com.xu.maker;

import com.xu.maker.generator.main.MainGenerator;

public class Main {

    public static void main(String[] args) throws Exception {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}
