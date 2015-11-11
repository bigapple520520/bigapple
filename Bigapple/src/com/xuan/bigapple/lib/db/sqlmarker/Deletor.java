package com.xuan.bigapple.lib.db.sqlmarker;

import android.text.TextUtils;

import com.xuan.bigapple.lib.db.helper.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除构造器
 *
 * @author xuan
 */
public class Deletor {
    private final StringBuilder mSql;
    private final List<String> mArgList;

    /**
     * 构造方法
     *
     * @param tableName 表名
     */
    private Deletor(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("TableName can't be null.");
        }

        mSql = new StringBuilder();
        mArgList = new ArrayList<String>();
        mSql.append("DELETE FROM " + tableName.trim());
    }

    /**
     * 删除实例
     *
     * @param tableName
     * @return
     */
    public static Deletor deleteFrom(String tableName) {
        return new Deletor(tableName);
    }

    /**
     * 增加查询条件
     *
     * @param operator   操作，比如：AND、OR
     * @param expression 表达式，比如：id=?
     * @param arg        表达式中的参数的值
     */
    public Deletor addExpression(String operator, String expression, String arg) {
        mSql.append(" " + operator);
        mSql.append(" " + expression);
        mArgList.add(arg);
        return this;
    }

    /**
     * 第一个查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Deletor where(String expression, String arg) {
        addExpression("WHERE", expression, arg);
        return this;
    }

    /**
     * 增加AND查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Deletor and(String expression, String arg) {
        addExpression("AND", expression, arg);
        return this;
    }

    /**
     * 增加OR查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Deletor or(String expression, String arg) {
        addExpression("OR", expression, arg);
        return this;
    }

    /**
     * Where In查询
     *
     * @param columnName
     * @param inArgList
     * @return
     */
    public Deletor whereIn(String columnName, List<String> inArgList) {
        mSql.append(" AND ");
        mSql.append(columnName + " IN " + SqlUtils.getInSQL(inArgList.size()));
        mArgList.addAll(inArgList);
        return this;
    }

    /**
     * And In查询
     *
     * @param columnName
     * @param inArgList
     * @return
     */
    public Deletor andIn(String columnName, List<String> inArgList) {
        mSql.append(" AND ");
        mSql.append(columnName + " IN " + SqlUtils.getInSQL(inArgList.size()));
        mArgList.addAll(inArgList);
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
        return mSql.toString();
    }

}
