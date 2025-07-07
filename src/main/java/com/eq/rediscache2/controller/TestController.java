package com.eq.rediscache2.controller;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.cache.core.EasyCacheClient;
import com.easy.query.core.basic.api.database.CodeFirstCommand;
import com.easy.query.core.basic.api.database.DatabaseCodeFirst;
import com.eq.rediscache2.domain.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * create time 2025/7/7 13:25
 * 文件说明
 *
 * @author xuejiaming
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/api/test")
public class TestController {

    private final EasyEntityQuery easyEntityQuery;
    private final EasyCacheClient easyCacheClient;


    @GetMapping("/clean")
    public Object clean() {
        easyEntityQuery.deletable(SysUser.class).disableLogicDelete().allowDeleteStatement(true).where(s -> {s.id().isNotNull();}).executeRows();
        return null;
    }
    @GetMapping("/insert")
    public Object insert() {
        DatabaseCodeFirst databaseCodeFirst = easyEntityQuery.getDatabaseCodeFirst();
        databaseCodeFirst.createDatabaseIfNotExists();
        CodeFirstCommand codeFirstCommand = databaseCodeFirst.syncTableCommand(Arrays.asList(SysUser.class));
        codeFirstCommand.executeWithTransaction(s->s.commit());

        SysUser sysUser = new SysUser();
        sysUser.setName("UserName");
        sysUser.setAge(1);
        easyEntityQuery.insertable(sysUser).executeRows();
        return sysUser;
    }

    @GetMapping("/updateEntity")
    public Object updateEntity() {
        SysUser sysUser = easyEntityQuery.queryable(SysUser.class).firstNotNull();
        sysUser.setName(UUID.randomUUID().toString());
        easyEntityQuery.updatable(sysUser).executeRows();
        return sysUser;
    }

    @GetMapping("/updateExpression")
    public Object updateExpression() {
        SysUser sysUser = easyEntityQuery.queryable(SysUser.class).firstNotNull();
        String newName = UUID.randomUUID().toString();
        sysUser.setName(newName);
        easyEntityQuery.updatable(SysUser.class)
                .setColumns(s -> {
                    s.name().set(newName);
                }).whereById(sysUser.getId()).executeRows();
        return sysUser;
    }

    @GetMapping("/updateEntityColumn")
    public Object updateEntityColumn() {
        SysUser sysUser = easyEntityQuery.queryable(SysUser.class).firstNotNull();
        String newName = UUID.randomUUID().toString();
        sysUser.setName(newName);

        easyEntityQuery.updatable(sysUser)
                .setColumns(s -> s.FETCHER.name())
                .executeRows();
        return sysUser;
    }
    @GetMapping("/getFirst")
    public Object getFirst() {
        return easyEntityQuery.queryable(SysUser.class).firstNotNull();
    }
    @GetMapping("/getById")
    public Object getById(@RequestParam("id") String id) {
        return easyCacheClient.kvStorage(SysUser.class).singleOrNull(id);
    }
    @GetMapping("/get1000ById")
    public Object get1000ById(@RequestParam("id") String id) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            easyCacheClient.kvStorage(SysUser.class).singleOrNull(id);
        }
        long end = System.currentTimeMillis();
        return (end-start)+"(ms)";
    }
    @GetMapping("/getdb1000ById")
    public Object getdb1000ById(@RequestParam("id") String id) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {

            easyEntityQuery.queryable(SysUser.class).whereById(id).singleNotNull();
        }
        long end = System.currentTimeMillis();
        return (end-start)+"(ms)";
    }
    @GetMapping("/deleteExpression")
    public Object deleteExpression() {
        SysUser sysUser = easyEntityQuery.queryable(SysUser.class).firstNotNull();
        easyEntityQuery.deletable(SysUser.class)
                .where(s -> {
                    s.id().eq(sysUser.getId());
                }).executeRows();
        return null;
    }
    @GetMapping("/deleteEntity")
    public Object deleteEntity() {
        SysUser sysUser = easyEntityQuery.queryable(SysUser.class).firstNotNull();
        easyEntityQuery.deletable(sysUser).executeRows();
        return null;
    }
}
