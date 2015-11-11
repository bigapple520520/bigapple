package com.xuan.bigapple.lib.db.sqlmarker;

import android.text.TextUtils;

import com.xuan.bigapple.lib.db.helper.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新构造器
 *
 * @author xuan
 */
public class Updator {
    private final StringBuilder mSql;
    private final List<String> mArgList;
    private boolean mHasSet;

    /**
     * 构造方法
     *
     * @param tableName 表名
     */
    private Updator(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("TableName can't be null.");
        }

        mSql = new StringBuilder();
        mArgList = new ArrayList<String>();
        mSql.append("UPDATE " + tableName.trim());
        this.mHasSet = false;
    }

    /**
     * 创建一个插入器
     *
     * @param tableName
     * @return
     */
    public static Updator update(String tableName) {
        return new Updator(tableName);
    }

    /**
     * 添加更新值
     *
     * @param columnName
     * @param arg
     * @return
     */
    public Updator set(String columnName, String arg) {
        if (!mHasSet) {
            mSql.append(" SET ");
            mHasSet = true;
        } else {
            mSql.append(",");
        }
        mSql.append(columnName + "=?");

        mArgList.add(arg);
        return this;
    }

    /**
     * 增加查询条件
     *
     * @param operator   操作，比如：AND、OR
     * @param expression 表达式，比如：id=?
     * @param arg        表达式中的参数的值
     */
    public Updator addExpression(String operator, String expression, String arg) {
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
    public Updator where(String expression, String arg) {
        addExpression("WHERE", expression, arg);
        return this;
    }

    /**
     * 增加AND查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Updator and(String expression, String arg) {
        addExpression("AND", expression, arg);
        return this;
    }

    /**
     * 增加OR查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Updator or(String expression, String arg) {
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
    public Updator whereIn(String columnName, List<String> inArgList) {
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
    public Updator andIn(String columnName, List<String> inArgList) {
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
