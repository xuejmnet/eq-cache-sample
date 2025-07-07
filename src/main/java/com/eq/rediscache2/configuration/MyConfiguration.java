package com.eq.rediscache2.configuration;

import com.easy.query.cache.core.EasyCacheClient;
import com.easy.query.cache.core.bootstrapper.EasyCacheBootstrapper;
import com.easy.query.cache.core.key.CacheKeysProvider;
import com.easy.query.cache.core.manager.EasyCacheManager;
import com.easy.query.cache.core.util.EasyCacheUtil;
import com.easy.query.core.api.client.EasyQueryClient;
import com.eq.rediscache2.cache.CacheOption;
import com.eq.rediscache2.cache.MyCacheCleaner;
import com.eq.rediscache2.cache.MyCacheKeysProvider;
import com.eq.rediscache2.cache.MyCacheManager;
import com.eq.rediscache2.cache.MyMultiCacheManager;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create time 2025/7/7 13:20
 * 文件说明
 *
 * @author xuejiaming
 */
@Configuration
public class MyConfiguration {
    @Bean
    public EasyCacheClient easyCacheClient(EasyQueryClient easyQueryClient, RedissonClient redissonClient, CacheKeysProvider cacheKeysProvider, CacheOption cacheOption) {
        EasyCacheClient easyCacheClient = EasyCacheBootstrapper.defaultBuilderConfiguration()
                .optionConfigure(op -> {
                    op.setKeyPrefix("CACHE");
                    op.setCacheIndex("INDEX");
                    op.setExpireMillisSeconds(1000 * 60 * 60);//缓存1小时
                    op.setValueNullExpireMillisSeconds(1000 * 10);//null值缓存10秒
                })
                .replaceService(EasyQueryClient.class, easyQueryClient)
                .replaceService(RedissonClient.class, redissonClient)
                .replaceService(cacheOption)
                .replaceService(EasyCacheManager.class, MyMultiCacheManager.class).build();

        easyQueryClient.addTriggerListener(triggerEvent -> {
            boolean cacheEntity = EasyCacheUtil.isCacheEntity(triggerEvent.getEntityClass());
            if (cacheEntity) {
                new MyCacheCleaner(triggerEvent, cacheKeysProvider, easyCacheClient).clean();
            }
        });
        return easyCacheClient;
    }

    @Bean
    public CacheKeysProvider cacheKeysProvider(EasyQueryClient easyQueryClient) {
        return new MyCacheKeysProvider(easyQueryClient);
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setConnectionMinimumIdleSize(10)
                .setDatabase(2)
                .setAddress("redis://127.0.0.1:55001");
        config.useSingleServer().setPassword("redispw");
        StringCodec codec = new StringCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }
}
