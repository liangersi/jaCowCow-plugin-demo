package com.thoughtworks;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

@Slf4j
public class JaCowCowPlugin implements Plugin<Project> {
    @SneakyThrows
    @Override
    public void apply(Project project) {
        log.info("test for jaCowCow plugin");
        File prePushShell = new File("./src/main/java/com/thoughtworks/prepare/pre-push");
        File runShell = new File("./src/main/java/com/thoughtworks/prepare/run.sh");
        CopyFile.copyFile(prePushShell, ".git/hooks");
        CopyFile.copyFile(runShell, "./");
    }
}
