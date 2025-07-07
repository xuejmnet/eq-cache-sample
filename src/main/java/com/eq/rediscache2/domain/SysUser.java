package com.eq.rediscache2.domain;

import com.easy.query.cache.core.CacheKvEntity;
import com.easy.query.cache.core.annotation.CacheEntitySchema;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.eq.rediscache2.cache.CacheMultiLevel;
import com.eq.rediscache2.domain.proxy.SysUserProxy;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * create time 2025/7/6 21:23
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
@Table("sys_user")
@EntityProxy
@CacheEntitySchema
public class SysUser extends BaseEntity implements CacheMultiLevel,CacheKvEntity,ProxyEntityAvailable<SysUser , SysUserProxy> {
    private String name;
    private Integer age;
}
