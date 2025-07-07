package com.eq.rediscache2.interceptor;

import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateEntityColumnInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateSetInterceptor;
import com.easy.query.core.expression.parser.core.base.ColumnOnlySelector;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.segment.index.EntitySegmentComparer;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import com.eq.rediscache2.domain.BaseEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * create time 2025/7/7 08:49
 * 文件说明
 *
 * @author xuejiaming
 */
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class MyEntityInterceptor implements EntityInterceptor, UpdateSetInterceptor, UpdateEntityColumnInterceptor {

    /**
     * 添加默认的数据
     *
     * @param entityClass
     * @param entityInsertExpressionBuilder
     * @param entity
     */
    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {

        BaseEntity baseEntity = (BaseEntity) entity;
        if (baseEntity.getCreateTime() == null) {
            baseEntity.setCreateTime(LocalDateTime.now());
        }
        if (baseEntity.getUpdateTime() == null) {
            baseEntity.setUpdateTime(LocalDateTime.now());
        }
        if (baseEntity.getId() == null) {
            baseEntity.setId(UUID.randomUUID().toString());
        }
    }

    /**
     * 添加更新对象参数
     *
     * @param entityClass
     * @param entityUpdateExpressionBuilder
     * @param entity
     */
    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        baseEntity.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 表达式更新set参数添加
     *
     * @param entityClass
     * @param entityUpdateExpressionBuilder
     * @param columnSetter
     */
    @Override
    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnSetter<Object> columnSetter) {
        EntitySegmentComparer updateTime = new EntitySegmentComparer(entityClass, "updateTime");
        columnSetter.getSQLBuilderSegment().forEach(k -> {
            updateTime.visit(k);
            return updateTime.isInSegment();
        });
        //是否已经set了
        if (!updateTime.isInSegment()) {
            columnSetter.set("updateTime", LocalDateTime.now());
        }
    }

    @Override
    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnOnlySelector<Object> columnSelector, Object entity) {

        EntitySegmentComparer updateTime = new EntitySegmentComparer(entityClass, "updateTime");
        columnSelector.getSQLSegmentBuilder().forEach(k -> {
            updateTime.visit(k);
            return updateTime.isInSegment();
        });
        if (!updateTime.isInSegment()) {
            columnSelector.column("updateTime");
        }
    }

    @Override
    public String name() {
        return "DEFAULT_INTERCEPTOR";
    }

    @Override
    public boolean apply(Class<?> entityClass) {
        return BaseEntity.class.isAssignableFrom(entityClass);
    }
}
