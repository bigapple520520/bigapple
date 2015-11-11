package com.xuan.bigapple.lib.db.sqlmarker;

import android.text.TextUtils;

import com.xuan.bigapple.lib.db.helper.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 插入构造器
 *
 * @author xuan
 */
public class Insertor {
    private final String mSql;
    private final List<String> mColumnList;
    private final List<String> mArgList;

    /**
     * 构造方法
     *
     * @param tableName       表名
     * @param isReplaceInsert 是否是replace into方式插入
     */
    private Insertor(String tableName, boolean isReplaceInsert) {
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("TableName can't be null.");
        }

        mColumnList = new ArrayList<String>();
        mArgList = new ArrayList<String>();

        if (isReplaceInsert) {
            mSql = "REPLACE INTO " + tableName;
        } else {
            mSql = "INSERT INTO " + tableName;
        }
    }

    /**
     * 创建一个插入器
     *
     * @param tableName
     * @return
     */
    public static Insertor insertInto(String tableName) {
        return new Insertor(tableName, false);
    }

    /**
     * 创建一个插入器
     *
     * @param tableName
     * @return
     */
    public static Insertor replaceInto(String tableName) {
        return new Insertor(tableName, true);
    }

    /**
     * 添加一个需要插入的值
     *
     * @param columnName
     * @param arg
     * @return
     */
    public Insertor value(String columnName, String arg) {
        mColumnList.add(columnName);
        mArgList.add(arg);
        return this;
    }

    /**
     * 取得所有参数的值数组
     *
     * @return 所有参数的值数组
     */
    public Object[] getArgs() {
        return mArgList.toArray(new String[mArgList.size()]);
    }

    /**
     * 取得最后生成查询sql
     *
     * @return 查询sql
     */
    public String getSQL() {
        if (null == mColumnList || mColumnList.isEmpty()) {
            throw new IllegalArgumentException("No column to insert.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(mSql + "(");
        for (int i = 0, n = mColumnList.size(); i < n; i++) {
            if (i == (n - 1)) {
                // 最后一个参数
                sb.append(mColumnList.get(i) + ") ");
            } else {
                sb.append(mColumnList.get(i) + ",");
            }
        }
        sb.append("VALUES" + SqlUtils.getInSQL(mColumnList.size()));

        return sb.toString();
    }

}
