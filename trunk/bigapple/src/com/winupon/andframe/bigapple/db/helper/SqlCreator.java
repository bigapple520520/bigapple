/* 
 * @(#)SqlCreator.java    Created on 2010-4-8
 * Copyright (c) 2010 ZDSoft Networks, Inc. All rights reserved.
 * $Id: SqlCreator.java 8036 2010-08-02 04:33:40Z huangwj $
 */
package com.winupon.andframe.bigapple.db.helper;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * 动态查询 SQL 语句生成工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-4-9 下午6:48:29 $
 */
public class SqlCreator {
    private final StringBuilder sql;
    private final List<String> args;
    private boolean hasOrderBy = false;
    private boolean hasWhere = true;
    private boolean isFirst = true;

    // //////////////////////////////////////////// 构造 ////////////////////////////////////////////////////////////////
    /**
     * 构造方法
     * 
     * @param baseSQL
     *            带有 WHERE 关键字的原始 sql
     */
    public SqlCreator(String baseSQL) {
        this(baseSQL, true);
    }

    /**
     * 构造方法
     * 
     * @param baseSQL
     *            原始 sql
     * @param hasWhere
     *            原始 sql 是否带有 WHERE 关键字
     */
    public SqlCreator(String baseSQL, boolean hasWhere) {
        if (TextUtils.isEmpty(baseSQL)) {
            throw new IllegalArgumentException("baseSQL can't be null");
        }

        args = new ArrayList<String>();
        sql = new StringBuilder();
        sql.append(baseSQL.trim());
        this.hasWhere = hasWhere;
    }

    /**
     * 增加查询条件
     * 
     * @param operator
     *            操作，比如：AND、OR
     * @param expression
     *            表达式，比如：id=1
     * @param precondition
     *            先决条件，当为true时才会增加查询条件，比如 user != null
     */
    public void addExpression(String operator, String expression, boolean precondition) {
        addExpression(operator, expression, null, precondition);
    }

    /**
     * 增加查询条件
     * 
     * @param operator
     *            操作，比如：AND、OR
     * @param expression
     *            表达式，比如：id=?
     * @param arg
     *            表达式中的参数的值
     * @param argType
     *            表达式中的参数的类型
     * @param precondition
     *            先决条件，当为true时才会增加查询条件，比如 id != null
     */
    public void addExpression(String operator, String expression, String arg, boolean precondition) {
        if (precondition) {
            if (isFirst) {
                if (hasWhere) {
                    if (!sql.toString().toLowerCase().endsWith("where")) {
                        sql.append(" " + operator);
                    }
                }
                else {
                    sql.append(" WHERE");
                }
                isFirst = false;
            }
            else {
                sql.append(" " + operator);
            }

            sql.append(" " + expression);

            if (arg != null) {
                args.add(arg);
            }
        }
    }

    /**
     * 增加AND查询条件
     * 
     * @param expression
     *            表达式
     * @param precondition
     *            先决条件
     */
    public void and(String expression, boolean precondition) {
        addExpression("AND", expression, precondition);
    }

    /**
     * 增加AND查询条件
     * 
     * @param expression
     *            表达式
     * @param arg
     *            参数的值
     * @param precondition
     *            先决条件
     */
    public void and(String expression, String arg, boolean precondition) {
        addExpression("AND", expression, arg, precondition);
    }

    /**
     * 增加OR查询条件
     * 
     * @param expression
     *            表达式
     * @param precondition
     *            先决条件
     */
    public void or(String expression, boolean precondition) {
        addExpression("OR", expression, precondition);
    }

    /**
     * 增加OR查询条件
     * 
     * @param expression
     *            表达式
     * @param arg
     *            参数的值
     * @param precondition
     *            先决条件
     */
    public void or(String expression, String arg, boolean precondition) {
        addExpression("OR", expression, arg, precondition);
    }

    /**
     * 添加 GROUP BY 语句。
     * 
     * @param columnNames
     *            列名
     */
    public void groupBy(String... columnNames) {
        if (columnNames == null || columnNames.length == 0) {
            return;
        }

        sql.append(" GROUP BY ");
        for (String columnName : columnNames) {
            sql.append(columnName).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length() - 1);
    }

    /**
     * 升序排序
     * 
     * @param columnName
     *            列名
     */
    public void orderBy(String columnName) {
        orderBy(columnName, false);
    }

    /**
     * 降序排序
     * 
     * @param columnName
     *            列名
     */
    public void orderByDesc(String columnName) {
        orderBy(columnName, true);
    }

    /**
     * 排序
     * 
     * @param columnName
     *            列名
     * @param isDesc
     *            是否降序
     */
    public void orderBy(String columnName, boolean isDesc) {
        if (!hasOrderBy) {
            sql.append(" ORDER BY ");
        }
        else {
            sql.append(", ");
        }

        sql.append(columnName);
        if (isDesc) {
            sql.append(" DESC");
        }

        hasOrderBy = true;
    }

    /**
     * 取得所有参数的值数组
     * 
     * @return 所有参数的值数组
     */
    public String[] getArgs() {
        return args.toArray(new String[args.size()]);
    }

    /**
     * 取得最后生成查询sql
     * 
     * @return 查询sql
     */
    public String getSQL() {
        return sql.toString();
    }

    /**
     * 根据参数个数生成IN括弧里面的部分sql，包含括弧
     * 
     * @param size
     *            参数个数
     * @return IN括弧里面的部分sql
     */
    public static String getInSQL(int size) {
        StringBuilder inSQL = new StringBuilder();

        inSQL.append("(");
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                inSQL.append("?");
            }
            else {
                inSQL.append(",?");
            }
        }
        inSQL.append(")");

        return inSQL.toString();
    }

}
