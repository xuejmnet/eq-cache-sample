package com.eq.rediscache2.domain;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.LogicDelete;
import com.easy.query.core.annotation.UpdateIgnore;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * create time 2025/7/7 08:50
 * 文件说明
 *
 * @author xuejiaming
 */
@Data
public class BaseEntity {
    @Column(primaryKey = true)
    private String id;
    @UpdateIgnore
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @LogicDelete(strategy = LogicDeleteStrategyEnum.LOCAL_DATE_TIME)
    @UpdateIgnore
    private LocalDateTime deleteTime;
}
