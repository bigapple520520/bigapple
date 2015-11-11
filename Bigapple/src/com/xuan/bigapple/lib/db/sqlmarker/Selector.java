package com.xuan.bigapple.lib.db.sqlmarker;

import android.text.TextUtils;

import com.xuan.bigapple.lib.db.helper.SqlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询构造器
 *
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-4-9 下午6:48:29 $
 */
public class Selector {
    private final StringBuilder mSql;
    private final List<String> mArgList;
    private boolean mHasOrderBy;

    /**
     * 构造方法
     *
     * @param tableName 表名
     */
    private Selector(String tableName) {
        if (TextUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("TableName can't be null.");
        }

        mArgList = new ArrayList<String>();
        mSql = new StringBuilder();
        mSql.append("SELECT * FROM " + tableName.trim());
        this.mHasOrderBy = false;
    }

    /**
     * 创建一个选择器
     *
     * @param tableName
     * @return
     */
    public static Selector from(String tableName) {
        return new Selector(tableName);
    }

    /**
     * 增加查询条件
     *
     * @param operator   操作，比如：AND、OR
     * @param expression 表达式，比如：id=?
     * @param arg        表达式中的参数的值
     */
    public Selector addExpression(String operator, String expression, String arg) {
        mSql.append(" " + operator);
        mSql.append(" " + expression);

        if (arg != null) {
            mArgList.add(arg);
        }
        return this;
    }

    /**
     * 第一个查询条件
     *
     * @param expression
     * @param arg
     * @return
     */
    public Selector where(String expression, String arg) {
        addExpression("WHERE", expression, arg);
        return this;
    }

    /**
     * 增加AND查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Selector and(String expression, String arg) {
        addExpression("AND", expression, arg);
        return this;
    }

    /**
     * 增加OR查询条件
     *
     * @param expression 表达式
     * @param arg        参数的值
     */
    public Selector or(String expression, String arg) {
        addExpression("OR", expression, arg);
        return this;
    }

    /**
     * 添加 GROUP BY 语句。
     *
     * @param columnNames 列名
     */
    public Selector groupBy(String... columnNames) {
        if (null == columnNames || 0 == columnNames.length) {
            return this;
        }

        mSql.append(" GROUP BY ");
        for (String columnName : columnNames) {
            mSql.append(columnName).append(", ");
        }
        mSql.delete(mSql.length() - 2, mSql.length() - 1);// 删除最后一个逗号
        return this;
    }

    /**
     * 升序排序
     *
     * @param columnName 列名
     */
    public Selector orderByAsc(String columnName) {
        orderBy(columnName, false);
        return this;
    }

    /**
     * 降序排序
     *
     * @param columnName 列名
     */
    public Selector orderByDesc(String columnName) {
        orderBy(columnName, true);
        return this;
    }

    /**
     * 排序
     *
     * @param columnName 列名
     * @param isDesc     是否降序
     */
    public Selector orderBy(String columnName, boolean isDesc) {
        if (!mHasOrderBy) {
            mSql.append(" ORDER BY ");
        } else {
            mSql.append(", ");
        }

        mSql.append(columnName);
        if (isDesc) {
            mSql.append(" DESC");
        } else {
            mSql.append(" ASC");
        }

        mHasOrderBy = true;
        return this;
    }

    /**
     * limit分页部分语句
     *
     * @param num
     * @param offset
     * @return
     */
    public Selector limit(int num, int offset) {
        mSql.append(" LIMIT " + num + " OFFSET " + offset);
        return this;
    }

    /**
     * limit分页部分语句
     *
     * @param num
     * @return
     */
    public Selector limit(int num) {
        return limit(num, 0);
    }

    /**
     * In查询
     *
     * @param columnName
     * @param inArgList
     * @return
     */
    public Selector whereIn(String columnName, List<String> inArgList) {
        mSql.append(" WHERE ");
        mSql.append(columnName + " IN " + SqlUtils.getInSQL(inArgList.size()));
        mArgList.addAll(inArgList);
        return this;
    }

    /**
     * In查询
     *
     * @param columnName
     * @param inArgList
     * @return
     */
    public Selector andIn(String columnName, List<String> inArgList) {
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
    public String[] getArgs() {
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
