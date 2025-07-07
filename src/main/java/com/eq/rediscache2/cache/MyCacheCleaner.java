package com.eq.rediscache2.cache;

import com.easy.query.cache.core.EasyCacheClient;
import com.easy.query.cache.core.common.CacheKey;
import com.easy.query.cache.core.key.CacheKeysProvider;
import com.easy.query.core.basic.jdbc.conn.ConnectionManager;
import com.easy.query.core.basic.jdbc.tx.Transaction;
import com.easy.query.core.basic.jdbc.tx.TransactionListener;
import com.easy.query.core.context.QueryRuntimeContext;
import com.easy.query.core.trigger.TriggerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create time 2025/7/7 13:21
 * 文件说明
 *
 * @author xuejiaming
 */
@Slf4j
public class MyCacheCleaner {
    private static final ExecutorService _commonExecutor;

    static {
        _commonExecutor = Executors.newCachedThreadPool();
    }

    private final TriggerEvent triggerEvent;
    private final CacheKeysProvider cacheKeysProvider;
    private final EasyCacheClient easyCacheClient;

    public MyCacheCleaner(TriggerEvent triggerEvent, CacheKeysProvider cacheKeysProvider, EasyCacheClient easyCacheClient) {
        this.triggerEvent = triggerEvent;
        this.cacheKeysProvider = cacheKeysProvider;
        this.easyCacheClient = easyCacheClient;
    }

    public void clean() {
        QueryRuntimeContext runtimeContext = triggerEvent.getRuntimeContext();
        ConnectionManager connectionManager = runtimeContext.getConnectionManager();
        boolean inTransaction = connectionManager.currentThreadInTransaction();
        if (inTransaction) {
            Transaction transaction = connectionManager.getTransactionOrNull();
            if (transaction == null) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        clearTrigger();
                    }
                });
            } else {
                transaction.registerListener(new TransactionListener() {
                    @Override
                    public void afterCommit() {
                        clearTrigger();
                    }
                });
            }
        } else {
            clearTrigger();
        }
    }

    private void clearTrigger() {
        _commonExecutor.submit(() -> {
           try {
               List<CacheKey> cacheKeys = cacheKeysProvider.getCacheKeys(triggerEvent);
               for (CacheKey cacheKey : cacheKeys) {
                   easyCacheClient.deleteBy(cacheKey);
               }
           }catch (Exception ex){
               log.error("clearTrigger error:{}",ex.getMessage(),ex);
           }
        });
    }
}