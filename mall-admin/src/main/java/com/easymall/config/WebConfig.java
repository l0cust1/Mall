package com.easymall.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类 - 静态资源映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private AppConfig appConfig;

    /**
     * 配置静态资源映射
     * 将 /file/** 请求映射到实际的文件存储路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取文件存储根目录
        String fileRootPath = appConfig.getProjectFolder();
        
        // 确保路径以分隔符结尾
        if (!fileRootPath.endsWith("/") && !fileRootPath.endsWith("\\")) {
            fileRootPath += "/";
        }
        
        // 映射 /file/** 到实际文件路径（指向 file 子目录）
        registry.addResourceHandler("/file/**")
                .addResourceLocations("file:" + fileRootPath + "file/");
    }

}
