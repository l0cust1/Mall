package com.easymall.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 自动填充处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 如果 propertyId 为空，自动生成
        Object propertyId = getFieldValByName("propertyId", metaObject);
        if (propertyId == null || ((String) propertyId).isEmpty()) {
            this.setFieldValByName("propertyId", generateShortId(), metaObject);
        }
        
        // 如果 propertySort 为空，默认设置为 0
        Object propertySort = getFieldValByName("propertySort", metaObject);
        if (propertySort == null) {
            this.setFieldValByName("propertySort", 0, metaObject);
        }
    }

    /**
     * 更新时不自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时不做任何处理
    }
    
    /**
     * 生成短ID（秒级时间戳+随机数，最多13位）
     */
    private String generateShortId() {
        // 格式：秒级时间戳(10位) + 3位随机数 = 13位
        long timestamp = System.currentTimeMillis() / 1000;  // 秒级时间戳
        int random = (int) (Math.random() * 1000);
        return String.format("%d%03d", timestamp, random);
    }
}
