package com.xuan.bigapple.lib.db.callback.impl;

import android.database.Cursor;

import com.xuan.bigapple.lib.db.callback.SingleRowMapper;

import java.sql.SQLException;
import java.util.Map;

/**
 * 通用单条结果集
 *
 * @param <T>
 * @author xuan
 */
public class DefaultSingleRowMapper<T> implements SingleRowMapper<T> {
    private final Class<?> clazz;
    /**
     * 需要赋值类属性对应的数据库字段关系
     */
    private final Map<String, String> mColumnName2ClazzFieldMapping;

    public DefaultSingleRowMapper(Class<?> clazz,
                                  Map<String, String> columnName2ClazzFieldMapping) {
        this.clazz = clazz;
        this.mColumnName2ClazzFieldMapping = columnName2ClazzFieldMapping;
    }

    @Override
    public T mapRow(Cursor cursor) throws SQLException {
        return new DefaultMultiRowMapper<T>(clazz,
                mColumnName2ClazzFieldMapping).mapRow(cursor, 0);
    }

}
