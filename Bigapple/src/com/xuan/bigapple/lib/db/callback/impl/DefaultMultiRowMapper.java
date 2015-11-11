package com.xuan.bigapple.lib.db.callback.impl;

import android.database.Cursor;

import com.xuan.bigapple.lib.db.callback.MultiRowMapper;
import com.xuan.bigapple.lib.utils.DateUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * 通用结果集合
 *
 * @param <T>
 * @author xuan
 */
public class DefaultMultiRowMapper<T> implements MultiRowMapper<T> {
    /**
     * 需要返回的类Class
     */
    private final Class<?> mClazz;
    /**
     * 需要赋值类属性对应的数据库字段关系
     */
    private final Map<String, String> mColumnName2ClazzFieldMapping;

    public DefaultMultiRowMapper(Class<?> clazz,
                                 Map<String, String> columnName2ClazzFieldMapping) {
        this.mClazz = clazz;
        this.mColumnName2ClazzFieldMapping = columnName2ClazzFieldMapping;
    }

    @Override
    public T mapRow(Cursor cursor, int n) throws SQLException {
        Object obj = null;
        try {
            obj = mClazz.newInstance();

            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                Field field = findFieldByColumeName(columnName, obj);
                if (null == field) {
                    continue;
                }

                field.setAccessible(true);
                Class<?> fieldClass = field.getType();
                if (fieldClass == Date.class) {
                    // 日期转日期
                    field.set(obj, DateUtils.string2DateTime(cursor
                            .getString(cursor.getColumnIndex(columnName))));
                } else if (fieldClass == Short.class) {
                    field.setShort(obj,
                            cursor.getShort(cursor.getColumnIndex(columnName)));
                } else if (fieldClass == Integer.class) {
                    field.setInt(obj,
                            cursor.getInt(cursor.getColumnIndex(columnName)));
                } else if (fieldClass == Long.class) {
                    field.setLong(obj,
                            cursor.getLong(cursor.getColumnIndex(columnName)));
                } else if (fieldClass == Float.class) {
                    field.setFloat(obj,
                            cursor.getFloat(cursor.getColumnIndex(columnName)));
                } else if (fieldClass == Double.class) {
                    field.setDouble(obj,
                            cursor.getDouble(cursor.getColumnIndex(columnName)));
                } else {
                    field.set(obj,
                            cursor.getString(cursor.getColumnIndex(columnName)));
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }

        return (T) obj;
    }

    // 根据数据属性字段名称找类的属性
    private Field findFieldByColumeName(String columnName, Object obj) {
        Field field = null;
        String clazzFieldName = null;
        try {
            // 先尝试从mColumnName2ClazzMapping中获取属性名称
            clazzFieldName = mColumnName2ClazzFieldMapping.get(columnName);
            if (null == clazzFieldName) {
                // 找不到就用columnName充当
                clazzFieldName = columnName;
            }

            field = obj.getClass().getDeclaredField(clazzFieldName);
            if (null == field) {
                // 找不到属性对象
                LogUtils.e("Can not find field by columnName[" + columnName
                        + "]");
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }

        return field;
    }

}
