package com.eq.rediscache2.cache;

import com.easy.query.cache.core.annotation.CacheEntitySchema;
import com.easy.query.cache.core.base.CacheMethodEnum;
import com.easy.query.cache.core.key.AbstractCacheKeysProvider;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.exception.EasyQueryInvalidOperationException;
import com.easy.query.core.func.def.enums.TimeUnitEnum;
import com.easy.query.core.metadata.EntityMetadata;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * create time 2025/7/6 21:29
 * 文件说明
 *
 * @author xuejiaming
 */
public class MyCacheKeysProvider extends AbstractCacheKeysProvider {
    private final EasyQueryClient easyQueryClient;

    public MyCacheKeysProvider(EasyQueryClient easyQueryClient) {
        super(easyQueryClient);
        this.easyQueryClient = easyQueryClient;
    }

    @Override
    protected List<String> getCacheKeysByExpression(LocalDateTime triggerTime, LocalDateTime receivedTime, EntityMetadata entityMetadata, CacheMethodEnum clearMethod, CacheEntitySchema cacheEntitySchema) {
        LocalDateTime endTime = receivedTime.plusSeconds(1);
        if (CacheMethodEnum.UPDATE == clearMethod) {
            return easyQueryClient.queryable(entityMetadata.getEntityClass())
                    .where(o -> o.ge("updateTime", triggerTime).le("updateTime", endTime))
                    .select(String.class, o -> {
                        o.column(cacheEntitySchema.value());
                    })
                    .toList();
        }
        if (CacheMethodEnum.DELETE == clearMethod) {

            return easyQueryClient.queryable(entityMetadata.getEntityClass())
                    .disableLogicDelete()
                    .where(o -> o.ge("deleteTime", triggerTime).le("deleteTime", endTime))
                    .select(String.class, t -> {
                        t.column(cacheEntitySchema.value());
                    })
                    .toList();
        }
        if (CacheMethodEnum.INSERT == clearMethod) {

            return easyQueryClient.queryable(entityMetadata.getEntityClass())
                    .where(o -> o.ge("createTime", triggerTime).le("createTime", endTime))
                    .select(String.class, t -> {
                        t.column(cacheEntitySchema.value());
                    })
                    .toList();
        }

        throw new EasyQueryInvalidOperationException("cant get expression cache keys");
    }
}
