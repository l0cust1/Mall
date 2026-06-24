package com.easymall.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Configuration("appConfig")
@Data
public class AppConfig {
    @Value("${admin.account:}")
    private String adminAccount;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${project.folder:}")
    private String projectFolder;

    @PostConstruct
    public void validateConfig() {
        if (adminAccount == null || adminAccount.trim().isEmpty()) {
            throw new IllegalStateException("配置项 admin.account 不能为空");
        }
        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            throw new IllegalStateException("配置项 admin.password 不能为空");
        }
        if (projectFolder == null || projectFolder.trim().isEmpty()) {
            throw new IllegalStateException("配置项 project.folder 不能为空");
        }
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "adminAccount='" + adminAccount + '\'' +
                ", adminPassword='******'" +
                ", projectFolder='" + projectFolder + '\'' +
                '}';
    }
}
