package com.xuan.bigapple.lib.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xuan.bigapple.lib.utils.DateUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 操作数据库的工具类
 *
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-18 下午2:39:02 $
 */
public abstract class DbUtils {
    private static Map<String, Set<String>> table2ColumSetCache = new HashMap<String, Set<String>>();

    /**
     * 获取表的所有字段，带有缓存
     *
     * @param sqliteDatabase
     * @param tableName
     * @return
     */
    public static Set<String> getTableAllColumns(SQLiteDatabase sqliteDatabase,
                                                 String tableName) {
        if (table2ColumSetCache.containsKey(tableName)) {
            return table2ColumSetCache.get(tableName);
        } else {
            Set<String> columnSet = new HashSet<String>();
            Cursor cursor = null;
            try {
                cursor = sqliteDatabase.rawQuery("SELECT * FROM " + tableName
                        + " LIMIT 0", null);
                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    columnSet.add(column);
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            } finally {
                if (null != cursor && !cursor.isClosed()) {
                    cursor.close();
                    cursor = null;
                }
            }

            table2ColumSetCache.put(tableName, columnSet);
            return columnSet;
        }
    }

    /**
     * 跟表字段匹配，利用返回整理出需要插入的字段
     *
     * @param entity
     * @param columnSet
     * @return
     */
    public static ContentValues getWantToInsertValues(Object entity,
                                                      Set<String> columnSet) {
        ContentValues values = new ContentValues();
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                if (columnSet.contains(name)) {
                    putValueToContentValues(field.get(entity), values, name);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }

        return values;
    }

    /**
     * 判断类型，并把值放入ContentValues对象中
     *
     * @param value
     * @param values
     * @param name
     */
    private static void putValueToContentValues(Object value,
                                                ContentValues values, String name) {
        if (value instanceof Integer) {
            values.put(name, (Integer) value);
        } else if (value instanceof Long) {
            values.put(name, (Long) value);
        } else if (value instanceof Byte) {
            values.put(name, (Byte) value);
        } else if (value instanceof byte[]) {
            values.put(name, (byte[]) value);
        } else if (value instanceof Boolean) {
            values.put(name, (Boolean) value);
        } else if (value instanceof Short) {
            values.put(name, (Short) value);
        } else if (value instanceof Float) {
            values.put(name, (Float) value);
        } else if (value instanceof Double) {
            values.put(name, (Double) value);
        } else if (value instanceof String) {
            values.put(name, (String) value);
        } else if (value instanceof Date) {
            values.put(name, DateUtils.date2StringBySecond((Date) value));
        } else {
            values.put(name, value.toString());
        }
    }

}
