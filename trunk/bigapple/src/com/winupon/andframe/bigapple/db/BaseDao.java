/* 
 * @(#)BaseDao.java    Created on 2014-8-18
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.db;

import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.winupon.andframe.bigapple.db.helper.DbUtils;
import com.winupon.andframe.bigapple.utils.Validators;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 继承该类可以使用反射操作数据库，使调用者更加简单好用，添加删除查询一句话搞定
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-18 上午11:48:30 $
 */
public class BaseDao {
    public static boolean DEBUG = false;

    /**
     * 用该锁来保证所有继承BaseDao实例的数据库操作线程安全
     */
    private final static ReentrantLock lock = new ReentrantLock();// 保证多线程访问数据库的安全，性能有所损失

    /**
     * 获取数据库，对应调用这个一次必须调用close关闭源
     * 
     * @return
     */
    public SQLiteDatabase openSQLiteDatabase() {
        return DBHelper.getInstance().getWritableDatabase();
    }

    /**
     * 使用完后请Close数据库连接，dbHelper的close其实内部就是sqliteDatabase的close
     */
    public void closeSQLiteDatabase() {
        DBHelper.getInstance().close();
    }

    /**
     * 用原生的sqlite执行语法
     * 
     * @param doGrammar
     */
    protected Object execute(DoGrammarInterface doGrammar) {
        if (null == doGrammar) {
            return null;
        }

        Object retObj = null;
        lock.lock();
        try {
            SQLiteDatabase sqliteDatabase = openSQLiteDatabase();
            retObj = doGrammar.doGrammar(sqliteDatabase);
        }
        catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
        finally {
            closeSQLiteDatabase();
            lock.unlock();
        }
        return retObj;
    }

    /**
     * 执行语法回调接口
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2014-8-18 上午10:35:17 $
     */
    public interface DoGrammarInterface {
        Object doGrammar(SQLiteDatabase sqliteDatabase);
    }

    /**
     * 插入数据到数据库，支持批量插入
     * 
     * @param tableName
     *            数据库表名
     * @param entitys
     *            需要插入的数据对象
     * @return
     */
    public long insert(String tableName, Object... entitys) {
        if (Validators.isEmpty(tableName) || Validators.isEmpty(entitys)) {
            return 0;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        long insertCount = 0;
        try {
            sqliteDatabase = openSQLiteDatabase();
            Set<String> columnSet = DbUtils.getTableAllColumns(sqliteDatabase, tableName);

            sqliteDatabase.beginTransaction();
            for (Object entity : entitys) {
                ContentValues values = DbUtils.getWantToInsertValues(entity, columnSet);
                insertCount = sqliteDatabase.insert(tableName, null, values);
            }

            sqliteDatabase.setTransactionSuccessful();
        }
        catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
        finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }

            closeSQLiteDatabase();
            lock.unlock();
        }

        return insertCount;
    }

}
