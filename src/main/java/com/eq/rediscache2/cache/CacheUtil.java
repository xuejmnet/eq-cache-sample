package com.eq.rediscache2.cache;


/**
 * create time 2025/7/7 16:32
 * 文件说明
 *
 * @author xuejiaming
 */
public class CacheUtil {
    public static boolean isMultiCacheEntity(Class<?> entityClass) {
        return CacheMultiLevel.class.isAssignableFrom(entityClass);
    }
}
