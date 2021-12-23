package com.thoughtworks;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

@Slf4j
public class JaCowCowPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        log.info("test for jaCowCow plugin");
    }
}
