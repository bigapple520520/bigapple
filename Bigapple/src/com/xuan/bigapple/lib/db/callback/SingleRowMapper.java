package com.xuan.bigapple.lib.db.callback;

import android.database.Cursor;

import java.sql.SQLException;

/**
 * 用来处理单行记录集的情况的接口
 *
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-4-9 下午6:48:09 $
 */
public interface SingleRowMapper<T> {

    /**
     * 把结果集的这行记录映射成一个实体对象，方法里不需要执行 <code>rs.next()</code>。
     *
     * @param rs 结果集
     * @return 实体对象
     * @throws SQLException 在数据库发生错误时抛出此异常
     */
    T mapRow(Cursor rs) throws SQLException;

}
