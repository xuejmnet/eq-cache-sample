package com.eq.rediscache2.cache;

import com.easy.query.cache.core.EasyCacheOption;
import com.easy.query.cache.core.common.CacheItem;
import com.easy.query.cache.core.common.CacheKey;
import com.easy.query.cache.core.manager.AbstractCacheManager;
import com.eq.rediscache2.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.time.Duration;

/**
 * create time 2025/7/6 21:30
 * 文件说明
 *
 * @author xuejiaming
 */
@Slf4j
public class MyCacheManager extends AbstractCacheManager {

    private final RedissonClient redissonClient;

    public MyCacheManager(RedissonClient redissonClient, EasyCacheOption easyCacheOption) {
        super(easyCacheOption);
        this.redissonClient = redissonClient;
    }

    @Nullable
    @Override
    public CacheItem getCacheItem(String cacheKey, String conditionKey, Class<?> entityClass) {
        String entityCacheKey = getCacheKey(entityClass, cacheKey);
        return getCacheItem(entityCacheKey, conditionKey);
    }
    private CacheItem getCacheItem(String key, String conditionKey) {
        return getRedissonCacheItem(key, conditionKey);
    }

    private CacheItem getRedissonCacheItem(String key, String conditionKey) {
        RMap<String, String> map = redissonClient.getMap(key);
        String cacheItemJson = map.get(conditionKey);
        if (cacheItemJson != null) {
            return fromJson(cacheItemJson, CacheItem.class);
        }
        return null;
    }


    @Override
    public void setCacheItem(String cacheKey, String conditionKey, CacheItem cacheItem, Class<?> entityClass, long expireMillisSeconds) {
        String entityCacheKey = getCacheKey(entityClass, cacheKey);
        RMap<String, String> entityJsonMap = redissonClient.getMap(entityCacheKey);
        boolean mapExists = entityJsonMap.isExists();
        entityJsonMap.put(conditionKey, toJson(cacheItem));
        if (!mapExists) {
            entityJsonMap.expire(Duration.ofMillis(expireMillisSeconds));
        }
    }

    @Override
    public <T> String toJson(T object) {
        return JsonUtil.object2JsonStr(object);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return JsonUtil.jsonStr2Object(json,clazz);
    }

    @Override
    protected void deleteBy0(CacheKey cacheKey) {
        log.info("操作:{},缓存:{},key:{}被删除",cacheKey.getCacheMethod().name(),cacheKey.getEntityClass().getName(),cacheKey.getKey());
        String deleteCacheKey = getCacheKey(cacheKey.getEntityClass(), cacheKey.getKey());
        redissonClient.getMap(deleteCacheKey).delete();
    }
}
