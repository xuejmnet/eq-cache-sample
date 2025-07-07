package com.eq.rediscache2.cache;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * create time 2025/7/7 16:39
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
@Component
public class CacheOption {
    /**
     * 默认过期时间 5分钟
     */
    @Value("${cache.memory-expire-millis-seconds}")
    private long memoryExpireMillisSeconds = 300000;
    /**
     * 默认内存初始化数量1000
     */
    @Value("${cache.memory-initial-capacity}")
    private int memoryInitialCapacity = 1000;
    /**
     * 默认内存最大数量10000
     */
    @Value("${cache.memory-maximum-size}")
    private int memoryMaximumSize = 10000;
}
