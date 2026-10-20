package com.easymall.controller;


import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.utils.FFmpegUtils;
import com.easymall.utils.FileUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController extends ABaseController {

    @Resource
    private FileUtils fileUtil;
    @Resource
    private FFmpegUtils ffmpegUtil;

    // 1. 上传
    @PostMapping("/upload")
    public ResponseVO upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileUtil.uploadImage(file);
            Map<String, String> map = new HashMap<>();
            map.put("fileName", fileName);
            map.put("url", "http://localhost:6061/" + fileName);
            return getSuccessResponseVO(map);
        } catch (Exception e) {
            return getFailResponseVO("上传失败：" + e.getMessage());
        }
    }

    // 2. 删除
    @DeleteMapping("/delete")
    public ResponseVO delete(@RequestParam("fileName") String fileName) {
        boolean flag = fileUtil.deleteFile(fileName);
        return flag ? getSuccessResponseVO("删除成功") : getFailResponseVO("文件不存在");
    }

    // 3. 添加文字水印
    @PostMapping("/add-text-watermark")
    public ResponseVO addTextWatermark(
            @RequestParam("fileName") String fileName,
            @RequestParam("text") String text) {
        try {
            // 清理文件名，移除可能导致问题的字符
            fileName = fileName.trim().replaceAll("[,\\s]+", "");

            if (fileName.isEmpty()) {
                return getFailResponseVO("文件名不能为空");
            }

            String source = fileUtil.getFilePath(fileName);
            String targetName = "text_wm_" + fileName;
            String target = fileUtil.getFilePath(targetName);

            System.out.println("=== 添加文字水印调试信息 ===");
            System.out.println("原始文件名: " + fileName);
            System.out.println("源文件路径: " + source);
            System.out.println("目标文件路径: " + target);
            System.out.println("水印文字: " + text);

            ffmpegUtil.addTextWatermark(source, target, text);

            Map<String, String> map = new HashMap<>();
            map.put("fileName", targetName);
            map.put("url", "http://localhost:6061/" + targetName);
            return getSuccessResponseVO(map);
        } catch (Exception e) {
            e.printStackTrace();
            return getFailResponseVO("水印失败：" + e.getMessage());
        }
    }

    // 4. 添加图片水印
    @PostMapping("/add-image-watermark")
    public ResponseVO addImageWatermark(
            @RequestParam("fileName") String fileName,
            @RequestParam("watermark") MultipartFile watermarkFile) {
        try {
            // 清理文件名，移除可能导致问题的字符
            fileName = fileName.trim().replaceAll("[,\\s]+", "");

            if (fileName.isEmpty()) {
                return getFailResponseVO("文件名不能为空");
            }

            if (watermarkFile == null || watermarkFile.isEmpty()) {
                return getFailResponseVO("水印文件不能为空");
            }

            String wmName = fileUtil.uploadImage(watermarkFile);
            String source = fileUtil.getFilePath(fileName);
            String wmPath = fileUtil.getFilePath(wmName);
            String targetName = "img_wm_" + fileName;
            String target = fileUtil.getFilePath(targetName);

            System.out.println("=== 添加图片水印调试信息 ===");
            System.out.println("原始文件名: " + fileName);
            System.out.println("源文件路径: " + source);
            System.out.println("水印文件路径: " + wmPath);
            System.out.println("目标文件路径: " + target);

            ffmpegUtil.addImageWatermark(source, wmPath, target);

            Map<String, String> map = new HashMap<>();
            map.put("fileName", targetName);
            map.put("url", "http://localhost:6061/" + targetName);
            return getSuccessResponseVO(map);
        } catch (Exception e) {
            e.printStackTrace();
            return getFailResponseVO("图片水印失败：" + e.getMessage());
        }
    }


}

