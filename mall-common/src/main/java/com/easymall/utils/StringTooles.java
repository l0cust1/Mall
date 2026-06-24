package com.easymall.utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * 字符串工具类
 * 提供通用的字符串处理、判空、加密、随机数生成等功能
 */
public class StringTooles {

    /**
     * 首字母大写（智能处理：第二个字母大写时不处理，避免破坏驼峰）
     * @param field 原字符串
     * @return 处理后的字符串
     */
    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        // 如果第二个字母是大写，不处理首字母（例如 userId 不需要变成 UserId）
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * 判断字符串是否为空（严格判空：null、空串、纯空格、"null"字符串、空字符都算空）
     * @param str 要判断的字符串
     * @return true=空，false=非空
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        String trimStr = str.trim();
        return "".equals(trimStr) || "null".equals(trimStr) || "\u0000".equals(trimStr);
    }

    /**
     * MD5 加密
     * @param originString 原始字符串
     * @return MD5 16进制字符串
     */
    public static String encodeByMD5(String originString) {
        if (isEmpty(originString)) {
            return null;
        }
        return DigestUtils.md5Hex(originString);
    }

    /**
     * SHA-256 Hash 加密（用于 propertyValueIds 等敏感数据）
     * @param originString 原始字符串
     * @return SHA-256 16进制字符串
     */
    public static String encodeBySHA256(String originString) {
        if (isEmpty(originString)) {
            return null;
        }
        return DigestUtils.sha256Hex(originString);
    }

    /**
     * 生成指定长度的纯数字随机数
     * @param length 长度
     * @return 随机数字字符串
     */
    public static String getRandomNumber(int length) {
        return RandomStringUtils.random(length, false, true);
    }

    /**
     * 生成指定长度的字母+数字混合随机字符串
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    /**
     * 生成3位随机数字（常用于文件后缀/文件名）
     * @return 3位数字
     */
    /**
     * 获取文件后缀名（不带点）
     * @param fileName 文件名：如 "avatar.png" / "report.pdf"
     * @return 后缀：png、pdf，无后缀则返回空字符串
     */
    public static String getFileSuffix(String fileName) {
        // 判空
        if (StringTooles.isEmpty(fileName)) {
            return "";
        }
        // 获取最后一个 . 的位置
        int lastIndex = fileName.lastIndexOf(".");
        // 没有后缀，返回空串
        if (lastIndex == -1 || lastIndex == fileName.length() - 1) {
            return "";
        }
        // 截取后缀
        return fileName.substring(lastIndex);
    }
}