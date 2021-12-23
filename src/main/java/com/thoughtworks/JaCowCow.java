package com.thoughtworks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JaCowCow {

    public static final String MINIMUM = "minimum";
    public static final String GRADLE_FILE_NAME = "build.gradle";

    public static double getCoverage(String projectPath) {
        String csvFile = "build/reports/coverage/unit/jacocoTestReport.csv"; //路径适配
        String line;
        double totalCoverage = 0;
        int index = 0;
        double missed = 0;
        double coveraged = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(projectPath + csvFile))) {
            // 抽方法
            while ((line = bufferedReader.readLine()) != null) {
                // use comma as separator
                String[] country = line.split(",");
                if (index > 0) {
                    coveraged += Double.parseDouble(country[4]);
                    missed += Double.parseDouble(country[3]);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (missed + coveraged == 0) {
            return totalCoverage;
        }
        double coverageTemp = coveraged / (missed + coveraged);
        totalCoverage = BigDecimal.valueOf(coverageTemp).setScale(3, RoundingMode.DOWN).doubleValue();
        return totalCoverage;
    }

    public static double getOriginCoverage(String projectPath) {
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(projectPath + GRADLE_FILE_NAME)) {
            props.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Double.parseDouble(props.getProperty(MINIMUM));
    }

    //写回文件
    //FileWriter 不允许你指定编码，使用 OutputStreamWriter 包装一个 FileOutputStream
    //并设置编码
    private static void writeFile(String filePath, String content) {
        BufferedWriter bufferedWriter = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //把文件内容找到并修改
    private static String readFileContent(String filepath, HashMap<String, String> hashMap) {
        StringBuilder fileContent = new StringBuilder();
        BufferedReader bufferedReader = null;
        try (
                FileInputStream fileInputStream = new FileInputStream(filepath);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {

            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                for (Map.Entry<String, String> entry : hashMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (line.contains(key) && line.contains(MINIMUM)) {
                        line = line.replace(key, value);
                    }
                }
                fileContent.append(line);
                fileContent.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    public static void main(String[] args) {
        double coverage = getCoverage(""); //得到新的覆盖率
        double originCoverage = getOriginCoverage(""); //得到配置文件中原来的覆盖率
        if (coverage > originCoverage) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(String.valueOf(originCoverage), String.valueOf(coverage));

            String totalContent = readFileContent(GRADLE_FILE_NAME, hashMap); //把文件内容找到并修改,然后全部返回
            writeFile(GRADLE_FILE_NAME, totalContent); //修改gradle文档，直接重新写入就可以了
        }
    }
}
