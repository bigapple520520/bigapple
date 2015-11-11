package com.xuan.bigapple.lib.db.callback.impl;

import android.database.Cursor;

import com.xuan.bigapple.lib.db.callback.MapRowMapper;

import java.sql.SQLException;
import java.util.Map;

/**
 * 查询结果以Map的形势返回
 *
 * @param <T>
 */
public class DefaultMapRowMapper<T> implements MapRowMapper<String, T> {
    private final String keyColumnName;// 需要做key的字段
    private final Class<?> clazz;
    /**
     * 需要赋值类属性对应的数据库字段关系
     */
    private final Map<String, String> mColumnName2ClazzFieldMapping;

    public DefaultMapRowMapper(String keyColumnName, Class<?> clazz,
                               Map<String, String> columnName2ClazzFieldMapping) {
        this.keyColumnName = keyColumnName;
        this.clazz = clazz;
        this.mColumnName2ClazzFieldMapping = columnName2ClazzFieldMapping;
    }

    @Override
    public String mapRowKey(Cursor cursor, int rowNum) throws SQLException {
        return cursor.getString(cursor.getColumnIndex(keyColumnName));
    }

    @Override
    public T mapRowValue(Cursor cursor, int rowNum) throws SQLException {
        return (T) new DefaultMultiRowMapper(clazz,
                mColumnName2ClazzFieldMapping).mapRow(cursor, rowNum);
    }

}
