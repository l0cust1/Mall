package com.easymall.entity.constants;

public class Constants {
    public static String ZERO_STR = "0";
    public static final Integer LENGTH_5 = 5;
    public static final Integer LENGTH_10 = 10;
    public static final Integer LENGTH_15 = 15;
    public static final Integer LENGTH_30 = 30;

    private static final String REDIS_KEY_PREFIX = "easymall:";
    public static final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";
    public static final String REDIS_KEY_TOKEN_ADMIN = REDIS_KEY_PREFIX + "token:admin:";
    public static final String REDIS_KEY_SHIPPING_INFO = REDIS_KEY_PREFIX + "shipping:info";
    public static final Long REDIS_KEY_EXPIRES_ONE_MIN = 60L;
    public static final Long REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;
    public static final String REDIS_KEY_PHONECHECK_CODE = REDIS_KEY_PREFIX + "checkPhoneCode:";
    public static final String REDIS_KEY_REFRESH_TOKEN = REDIS_KEY_PREFIX + "refreshToken:";
    public static final Long REDIS_KEY_EXPIRES_SEVEN_DAYS = REDIS_KEY_EXPIRES_DAY * 7;
    public static final Long ACCESS_TOKEN_EXPIRATION = 15 * 60L; // 15分钟（秒）
    public static final String TOKEN_ADMIN = "adminToken";
    public static final String COOKIE_REFRESH_TOKEN = "refresh_token";
    public static final String CACHE_KEY_CAROUSEL = "carousel:home:list";
    // 首页聚合缓存前缀（用于清除整个首页缓存）
    public static final String HOME_AGGREGATE_PREFIX = "home:aggregate:";
    public static final String FILE_FOLDER_FILE = "file/";
    public static final String IMAGE_THUMBNAIL_SUFFIX = "_thumbnail";

     public static final String CACHE_KEY_PREFIX = "browse:history:";
}