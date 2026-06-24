package com.easymall.entity.VO;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class SeckillVO implements Serializable {
    /** 秒杀开始时间（时间戳） */
    private Long startTime;

    /** 秒杀结束时间（时间戳） */
    private Long endTime;

    /** 秒杀商品列表 */
    private List<SeckillProductVO> products;
}