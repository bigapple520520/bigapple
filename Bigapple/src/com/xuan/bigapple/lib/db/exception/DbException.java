package com.xuan.bigapple.lib.db.exception;

/**
 * 自定义的一个数据库操作异常
 *
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-18 下午2:36:43 $
 */
public class DbException extends Exception {
    private static final long serialVersionUID = -1287845410412219321L;

    public DbException() {
    }

    public DbException(String message) {
        super(message);
    }

    public DbException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DbException(Throwable throwable) {
        super(throwable);
    }

}
