package com.easymall.utils;

import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class FFmpegUtils{

    /**
     * 添加 文字水印（FFmpeg 最佳命令）
     */
    public void addTextWatermark(String input, String output, String text) throws Exception {
        // 验证输入文件是否存在
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            throw new Exception("输入文件不存在: " + input);
        }
        
        // 对文字进行转义，防止特殊字符导致命令失败
        // 移除可能导致问题的字符
        String escapedText = text.replace("\\", "\\\\")
                                 .replace(":", "\\:")
                                 .replace("'", "\\'");
        
        // 使用 Windows 系统字体路径（直接使用 .ttf 文件，不依赖 fontconfig）
        String fontPath = "C\\:/Windows/Fonts/simhei.ttf";
        File fontFile = new File("C:/Windows/Fonts/simhei.ttf");
        if (!fontFile.exists()) {
            // 如果黑体不存在，尝试使用宋体
            fontPath = "C\\:/Windows/Fonts/simsun.ttc";
        }
        
        // 构建 FFmpeg 命令，使用双引号包裹所有参数
        String command = String.format(
                "ffmpeg -i \"%s\" -vf \"drawtext=fontfile='%s':text='%s':fontcolor=white:fontsize=30:x=w-tw-20:y=h-th-20:box=1:boxcolor=black@0.3\" -y \"%s\"",
                input.trim(), fontPath, escapedText, output.trim()
        );
        
        System.out.println("输入文件: " + input);
        System.out.println("输出文件: " + output);
        System.out.println("水印文字: " + text);
        System.out.println("执行命令: " + command);
        executeCommand(command);
    }

    /**
     * 添加 图片水印（右下角，半透明）
     */
    public void addImageWatermark(String input, String watermark, String output) throws Exception {
        String command = String.format(
                "ffmpeg -i \"%s\" -i \"%s\" -filter_complex \"overlay=W-w-20:H-h-20:alpha=0.5\" -y \"%s\"",
                input, watermark, output
        );
        executeCommand(command);
    }

    /**
     * 去除水印（使用 delogo 滤镜）
     * 适用：固定位置的矩形水印
     * x,y: 水印左上角坐标
     * w,h: 水印的宽度和高度
     */
    public void removeWatermark(String input, String output) throws Exception {
        // 第一步：获取图片尺寸
        String probeCommand = String.format("ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=p=0 \"%s\"", input);
        Process probeProcess = Runtime.getRuntime().exec(probeCommand);
        java.io.BufferedReader reader = new java.io.BufferedReader(
            new java.io.InputStreamReader(probeProcess.getInputStream()));
        String sizeInfo = reader.readLine();
        probeProcess.waitFor();
        probeProcess.destroy();
        
        if (sizeInfo == null || !sizeInfo.contains(",")) {
            throw new Exception("无法获取图片尺寸");
        }
        
        String[] parts = sizeInfo.split(",");
        int width = Integer.parseInt(parts[0].trim());
        int height = Integer.parseInt(parts[1].trim());
        
        System.out.println("图片尺寸: " + width + "x" + height);
        
        // 第二步：计算水印位置（右下角，水印大小为图片的 1/3）
        // 确保水印区域不超出图片边界
        int logoWidth = Math.min(200, width / 3);  // 最大200，不超过宽度的1/3
        int logoHeight = Math.min(100, height / 3); // 最大100，不超过高度的1/3
        int x = width - logoWidth;   // 右下角 X 坐标
        int y = height - logoHeight; // 右下角 Y 坐标
        
        // 确保坐标不小于 0
        x = Math.max(0, x);
        y = Math.max(0, y);
        
        System.out.println("水印区域: x=" + x + ", y=" + y + ", w=" + logoWidth + ", h=" + logoHeight);
        
        // 第三步：执行去水印命令
        String command = String.format(
                "ffmpeg -i \"%s\" -vf \"delogo=x=%d:y=%d:w=%d:h=%d:show=0\" -y \"%s\"",
                input, x, y, logoWidth, logoHeight, output
        );
        
        System.out.println("执行去水印命令: " + command);
        executeCommand(command);
    }

    // 执行系统命令
    private void executeCommand(String command) throws Exception {
        System.out.println("开始执行 FFmpeg 命令...");
        Process process = Runtime.getRuntime().exec(command);
        
        // 读取错误流（FFmpeg 的输出通常在错误流中）
        java.io.BufferedReader errorReader = new java.io.BufferedReader(
            new java.io.InputStreamReader(process.getErrorStream()));
        String line;
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        
        // 等待进程完成
        int exitCode = process.waitFor();
        process.destroy();
        
        if (exitCode != 0) {
            System.err.println("FFmpeg 执行失败，退出码: " + exitCode);
            System.err.println("错误信息: " + errorOutput.toString());
            throw new Exception("FFmpeg 处理失败: " + errorOutput.toString());
        }
        
        System.out.println("FFmpeg 命令执行成功");
    }
}