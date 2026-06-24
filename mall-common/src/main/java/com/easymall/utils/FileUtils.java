package com.easymall.utils;

import cn.hutool.core.date.DateUtil;
import com.easymall.config.AppConfig;
import com.easymall.entity.constants.Constants;
import com.easymall.enums.DateEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class FileUtils {
    @Value("${file.upload.path}")
    private String uploadPath;

    public String uploadImage(MultipartFile file) throws IOException {
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        File targetFile = new File(dir, fileName);
        file.transferTo(targetFile);
        return fileName;
    }

    public boolean deleteFile(String fileName) {
        File file = new File(uploadPath + fileName);
        return file.exists() && file.delete();
    }

    public String getFilePath(String fileName) {
        return uploadPath + fileName;
    }

}
