package com.winupon.andframe.bigapple.db.helper;

import com.winupon.andframe.bigapple.utils.DateUtils;

/**
 * Sql工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2012-11-22 上午10:13:34 $
 */
public abstract class SqlUtils {

    /**
     * 取得填充参数后的sql
     * 
     * @param preparedSQL
     *            预编译sql
     * @param args
     *            参数数组
     * @return 填充参数后的sql
     */
    public static String getSQL(String preparedSQL, Object[] args) {
        if (args == null || args.length == 0) {
            return preparedSQL;
        }

        StringBuilder sql = new StringBuilder();

        int index = 0;
        int parameterIndex = 0;

        while ((index = preparedSQL.indexOf('?')) > 0) {
            sql.append(preparedSQL.substring(0, index));
            preparedSQL = preparedSQL.substring(index + 1);

            Object arg = args[parameterIndex++];

            if (arg == null) {
                sql.append("null");
            }
            else if (arg instanceof String) {
                sql.append("'");
                sql.append(arg);
                sql.append("'");
            }
            else if (arg instanceof java.util.Date) {
                sql.append("'");
                sql.append(DateUtils.date2String((java.util.Date) arg));
                sql.append("'");
            }
            else {
                sql.append(arg);
            }
        }

        sql.append(preparedSQL);

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
