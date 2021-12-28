package com.thoughtworks.prepare;

import com.csvreader.CsvReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public class JaCowCow {

    public static final String MINIMUM = "minimum";
    public static final String GRADLE_FILE_NAME = "build.gradle";
    public static final String REPORT_FILE_PATH = "build/reports/coverage/unit/jacocoTestReport.csv";

    public static Double getReportValue(String filePath, TestCounter testCounter, TestValue testValue) {
        try {
            var csvReader = new CsvReader(filePath);
            var coveredCount = Double.valueOf(0);
            var missedCount = Double.valueOf(0);
            TestReportHeader[] testReportHeaders = TestReportHeader.getHeadersByCounter(testCounter);

            csvReader.readHeaders();
            while (csvReader.readRecord()) {
                coveredCount += Double.parseDouble(csvReader.get(String.valueOf(testReportHeaders[0])));
                missedCount += Double.parseDouble(csvReader.get(String.valueOf(testReportHeaders[1])));
            }
            return formatDoubleValue(TestValue.getValue(testValue, coveredCount, missedCount));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1.0;
    }

    private static Double formatDoubleValue(Double num) {
        return (double) Math.round(num * 1000) / 1000;
    }

    private static String getPropertyValue(String line, String pattern) {
        var p = Pattern.compile(pattern);
        var m = p.matcher(line);
        if (m.find()) {
            return m.group(3);
        }
        return null;
    }

    private static String getNewSettingFileContent(String settingFilePath, String reportFilePath) {
        var fileContent = new StringBuilder();
        try (
                var fileInputStream = new FileInputStream(settingFilePath);
                var inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {

            var bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            var isJacocoSetting = false;
            var jacocoSettingBrace = 0;
            var isViolationRules = false;
            var violationRulesBrace = 0;
            var isRule = false;
            var ruleBrace = 0;
            var isLimit = false;
            var limitBrace = 0;
            TestCounter counter = TestCounter.INSTRUCTION;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("jacocoTestCoverageVerification ")) {
                    isJacocoSetting = true;
                }
                jacocoSettingBrace = braceCounting(line, isJacocoSetting, jacocoSettingBrace);
                if (jacocoSettingBrace == 0) {
                    isJacocoSetting = false;
                }

                if (isJacocoSetting && line.contains("violationRules ")) {
                    isViolationRules = true;
                }
                violationRulesBrace = braceCounting(line, isViolationRules, violationRulesBrace);
                if (violationRulesBrace == 0) {
                    isViolationRules = false;
                }

                if (isViolationRules && line.contains("rule ")) {
                    isRule = true;
                }
                ruleBrace = braceCounting(line, isRule, ruleBrace);
                if (ruleBrace == 0) {
                    isRule = false;
                }

                if (isRule && line.contains("limit ")) {
                    isLimit = true;
                }
                limitBrace = braceCounting(line, isLimit, limitBrace);
                if (limitBrace == 0) {
                    isLimit = false;
                }

                if (isLimit && line.contains("counter")) {
                    String propertyValue = getPropertyValue(line, "(.*) (= '(.*)')");
                    counter = TestCounter.valueOf(propertyValue);
                }

                if (isLimit && line.contains(MINIMUM)) {
                    Double reportValue = Objects.requireNonNull(getReportValue(reportFilePath, counter, TestValue.COVEREDRATIO));
                    var originalValue = Double.valueOf(Objects.requireNonNull(getPropertyValue(line, "(.*) (= (.*))")));
                    if (reportValue > originalValue) {
                        line = line.replace(originalValue.toString(), reportValue.toString());
                    }
                    counter = TestCounter.INSTRUCTION;
                }
                fileContent.append(line);
                fileContent.append(System.getProperty("line.separator"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }

    private static int braceCounting(String line, boolean isInTheBrace, int settingBrace) {
        if (isInTheBrace) {
            if (line.contains("{")) {
                settingBrace += 1;
            }
            if (line.contains("}")) {
                settingBrace -= 1;
            }
        }
        return settingBrace;
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

    public static void main(String[] args) {
        String newSettingContent = getNewSettingFileContent(GRADLE_FILE_NAME, REPORT_FILE_PATH);
        writeFile(GRADLE_FILE_NAME, newSettingContent);
    }
}
