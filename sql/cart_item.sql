-- ============================================================
-- 购物车表
-- 数据库：easymall
-- 说明：同一用户同一 SKU 只保留一条记录，重复加购合并数量
-- ============================================================
CREATE TABLE IF NOT EXISTS `cart_item` (
    `id`                      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id`                 BIGINT       NOT NULL COMMENT '用户ID',
    `product_id`              VARCHAR(64)  NOT NULL COMMENT '商品ID',
    `sku_id`                  BIGINT       NOT NULL COMMENT 'SKU ID',
    `property_value_id_hash`  VARCHAR(128) NOT NULL COMMENT 'SKU 哈希，下单时传给 order/create',
    `quantity`                INT          NOT NULL DEFAULT 1 COMMENT '购买数量',
    `selected`                TINYINT      NOT NULL DEFAULT 1 COMMENT '是否勾选：1-是，0-否',
    `create_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`             DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_sku` (`user_id`, `sku_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '购物车表';
