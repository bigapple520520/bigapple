package com.xuan.bigapple.lib.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.xuan.bigapple.lib.db.callback.MapRowMapper;
import com.xuan.bigapple.lib.db.callback.MultiRowMapper;
import com.xuan.bigapple.lib.db.callback.SingleRowMapper;
import com.xuan.bigapple.lib.db.callback.impl.DefaultMapRowMapper;
import com.xuan.bigapple.lib.db.callback.impl.DefaultMultiRowMapper;
import com.xuan.bigapple.lib.db.callback.impl.DefaultSingleRowMapper;
import com.xuan.bigapple.lib.db.helper.DbUtils;
import com.xuan.bigapple.lib.db.helper.SqlUtils;
import com.xuan.bigapple.lib.db.sqlmarker.Deletor;
import com.xuan.bigapple.lib.db.sqlmarker.Insertor;
import com.xuan.bigapple.lib.db.sqlmarker.Selector;
import com.xuan.bigapple.lib.db.sqlmarker.Updator;
import com.xuan.bigapple.lib.utils.Validators;
import com.xuan.bigapple.lib.utils.log.LogUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对原生数据库操作做了一层轻量级封装，主要屏蔽了显式的close操作，并且处理了多线程操作的问题，当然也可以使用原生的API。<br>
 * 应用层可对本实例保持单例，线程安全。
 *
 * @author xuan
 */
public class BPBaseDao {
    public static boolean DEBUG = false;

    /**
     * 子类小心使用该锁，如果要使用请确保不会死锁，不然后果不堪
     */
    protected final static ReentrantLock lock = new ReentrantLock();// 保证多线程访问数据库的安全，性能有所损失

    /**
     * 获取数据库，对应调用这个一次必须调用close关闭源
     *
     * @return
     */
    public SQLiteDatabase openSQLiteDatabase() {
        return DBHelper.getInstance().getWritableDatabase();
    }

    /**
     * 使用完后请Close数据库连接，dbHelper的close其实内部就是sqliteDatabase的close，
     * 并且源码内部会判断null和open的状态
     */
    public void closeSQLiteDatabase() {
        DBHelper.getInstance().close();
    }

    /**
     * 用原生的sqlite执行语法
     *
     * @param onGrammarExcute
     */
    protected Object execute(OnGrammarExcute onGrammarExcute) {
        if (null == onGrammarExcute) {
            return null;
        }

        Object retObj = null;
        lock.lock();
        try {
            SQLiteDatabase sqliteDatabase = openSQLiteDatabase();
            retObj = onGrammarExcute.onExcute(sqliteDatabase);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeSQLiteDatabase();
            lock.unlock();
        }
        return retObj;
    }

    /**
     * Sql语句执行方法
     *
     * @param sql
     */
    protected void execSQL(String sql) {
        lock.lock();
        try {
            debugSql(sql, null);
            openSQLiteDatabase().execSQL(sql);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * Sql语句执行方法
     *
     * @param sql
     * @param bindArgs
     */
    protected void execSQL(String sql, Object[] bindArgs) {
        lock.lock();
        try {
            debugSql(sql, null);
            openSQLiteDatabase().execSQL(sql, bindArgs);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * 批量操作
     *
     * @param sql
     * @param bindArgsList
     */
    protected void execSQLBatch(String sql, List<Object[]> bindArgsList) {
        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = openSQLiteDatabase();
            sqliteDatabase.beginTransaction();

            for (int i = 0, n = bindArgsList.size(); i < n; i++) {
                Object[] bindArgs = bindArgsList.get(i);
                debugSql(sql, bindArgs);
                sqliteDatabase.execSQL(sql, bindArgs);
            }

            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * 插入或者更新
     *
     * @param sql
     */
    protected void bpUpdate(String sql) {
        lock.lock();
        try {
            debugSql(sql, null);
            openSQLiteDatabase().execSQL(sql);
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * 插入或者更新，带参
     *
     * @param sql
     * @param args
     */
    protected void bpUpdate(String sql, Object[] args) {
        if (null == args) {
            bpUpdate(sql);
        } else {
            lock.lock();
            try {
                debugSql(sql, args);
                openSQLiteDatabase().execSQL(sql, args);
            } catch (Exception e) {
                LogUtils.e(e.getMessage(), e);
            } finally {
                closeSQLiteDatabase();
                lock.unlock();
            }
        }
    }

    /**
     * 插入或者更新，批量
     *
     * @param sql
     * @param argsList
     */
    protected void bpUpdateBatch(String sql, List<Object[]> argsList) {
        if (null == argsList) {
            return;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = openSQLiteDatabase();
            sqliteDatabase.beginTransaction();
            for (Object[] args : argsList) {
                debugSql(sql, args);
                sqliteDatabase.execSQL(sql, args);
            }
            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * 查询，返回多条记录
     *
     * @param sql
     * @param args
     * @param multiRowMapper
     * @return
     */
    protected <T> List<T> bpQuery(String sql, String[] args,
                                  MultiRowMapper<T> multiRowMapper) {
        List<T> ret = new ArrayList<T>();

        lock.lock();
        debugSql(sql, args);
        Cursor cursor = null;
        try {
            cursor = openSQLiteDatabase().rawQuery(sql, args);
            int i = 0;
            while (cursor.moveToNext()) {
                T t = multiRowMapper.mapRow(cursor, i);
                ret.add(t);
                i++;
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeCursor(cursor);
            closeSQLiteDatabase();
            lock.unlock();
        }

        return ret;
    }

    /**
     * 查询，返回单条记录
     *
     * @param sql
     * @param args
     * @param singleRowMapper
     * @param <T>
     * @return
     */
    protected <T> T bpQuery(String sql, String[] args,
                            SingleRowMapper<T> singleRowMapper) {
        T ret = null;

        lock.lock();
        debugSql(sql, args);
        Cursor cursor = null;
        try {
            cursor = openSQLiteDatabase().rawQuery(sql, args);
            if (cursor.moveToNext()) {
                ret = singleRowMapper.mapRow(cursor);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeCursor(cursor);
            closeSQLiteDatabase();
            lock.unlock();
        }

        return ret;
    }

    /**
     * 查询，返回MAP集合
     *
     * @param sql
     * @param args
     * @param mapRowMapper
     * @param <K>
     * @param <V>
     * @return
     */
    protected <K, V> Map<K, V> bpQuery(String sql, String[] args,
                                       MapRowMapper<K, V> mapRowMapper) {
        Map<K, V> ret = new HashMap<K, V>();

        lock.lock();
        debugSql(sql, args);
        Cursor cursor = null;
        try {
            cursor = openSQLiteDatabase().rawQuery(sql, args);
            int i = 0;
            while (cursor.moveToNext()) {
                K k = mapRowMapper.mapRowKey(cursor, i);
                V v = mapRowMapper.mapRowValue(cursor, i);
                ret.put(k, v);
                i++;
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            closeCursor(cursor);
            closeSQLiteDatabase();
            lock.unlock();
        }

        return ret;
    }

    /**
     * IN查询，返回LIST集合
     *
     * @param prefix
     * @param prefixArgs
     * @param inArgs
     * @param postfix
     * @param multiRowMapper
     * @param <T>
     * @return
     */
    protected <T> List<T> bpQueryForInSQL(String prefix, String[] prefixArgs,
                                          String[] inArgs, String postfix, MultiRowMapper<T> multiRowMapper) {
        if (null == prefixArgs) {
            prefixArgs = new String[0];
        }

        StringBuilder sql = new StringBuilder();
        sql.append(prefix).append(SqlUtils.getInSQL(inArgs.length));

        if (!TextUtils.isEmpty(postfix)) {
            sql.append(postfix);
        }

        String[] args = new String[inArgs.length + prefixArgs.length];

        System.arraycopy(prefixArgs, 0, args, 0, prefixArgs.length);
        System.arraycopy(inArgs, 0, args, prefixArgs.length, inArgs.length);

        return bpQuery(sql.toString(), args, multiRowMapper);
    }

    /**
     * IN查询，返回MAP集合
     *
     * @param prefix
     * @param prefixArgs
     * @param inArgs
     * @param postfix
     * @param mapRowMapper
     * @param <K>
     * @param <V>
     * @return
     */
    protected <K, V> Map<K, V> queryForInSQL(String prefix,
                                             String[] prefixArgs, String[] inArgs, String postfix,
                                             MapRowMapper<K, V> mapRowMapper) {
        if (null == prefixArgs) {
            prefixArgs = new String[0];
        }

        StringBuilder sql = new StringBuilder();
        sql.append(prefix).append(SqlUtils.getInSQL(inArgs.length));

        if (!TextUtils.isEmpty(postfix)) {
            sql.append(postfix);
        }

        String[] args = new String[inArgs.length + prefixArgs.length];

        System.arraycopy(prefixArgs, 0, args, 0, prefixArgs.length);
        System.arraycopy(inArgs, 0, args, prefixArgs.length, inArgs.length);

        return bpQuery(sql.toString(), args, mapRowMapper);
    }

    private void debugSql(String sql, Object[] args) {
        if (DEBUG) {
            LogUtils.d(SqlUtils.getSQL(sql, args));
        }
    }

    // 关闭cursor
    private void closeCursor(Cursor cursor) {
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /**
     * 执行语法回调接口
     *
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2014-8-18 上午10:35:17 $
     */
    public interface OnGrammarExcute {
        /**
         * 执行语法的逻辑由用户自己实现
         *
         * @param sqliteDatabase
         * @return
         */
        Object onExcute(SQLiteDatabase sqliteDatabase);
    }

    // ----------------------------------利用反射操作数据API----------------------------------------------------------

    /**
     * 插入数据到数据库，支持批量插入
     *
     * @param tableName 数据库表名
     * @param entitys   需要插入的数据对象
     * @return
     */
    public long reflectInsert(String tableName, Object... entitys) {
        if (Validators.isEmpty(tableName) || Validators.isEmpty(entitys)) {
            return 0;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        long insertCount = 0;
        try {
            sqliteDatabase = openSQLiteDatabase();
            Set<String> columnSet = DbUtils.getTableAllColumns(sqliteDatabase,
                    tableName);

            sqliteDatabase.beginTransaction();
            for (Object entity : entitys) {
                ContentValues values = DbUtils.getWantToInsertValues(entity,
                        columnSet);
                insertCount = sqliteDatabase.insert(tableName, null, values);
            }

            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }

            closeSQLiteDatabase();
            lock.unlock();
        }

        return insertCount;
    }

    // //////////////////////利用语法构造器来进行操作//////////////////////////////////

    /**
     * 批量删除
     *
     * @param deletorList
     */
    protected void bpDeleteBatch(List<Deletor> deletorList) {
        if (null == deletorList || deletorList.isEmpty()) {
            return;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = openSQLiteDatabase();
            sqliteDatabase.beginTransaction();
            for (Deletor deletor : deletorList) {
                debugSql(deletor.getSQL(), deletor.getArgs());
                sqliteDatabase.execSQL(deletor.getSQL(), deletor.getArgs());
            }
            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * 删除
     *
     * @param deletor
     */
    protected void bpDetele(Deletor deletor) {
        if (null == deletor) {
            return;
        }

        bpUpdate(deletor.getSQL(), deletor.getArgs());
    }

    /**
     * Updator批量操作
     *
     * @param updatorList
     */
    protected void bpUpdateBatch(List<Updator> updatorList) {
        if (null == updatorList || updatorList.isEmpty()) {
            return;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = openSQLiteDatabase();
            sqliteDatabase.beginTransaction();
            for (Updator updator : updatorList) {
                debugSql(updator.getSQL(), updator.getArgs());
                sqliteDatabase.execSQL(updator.getSQL(), updator.getArgs());
            }
            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * Updator操作
     *
     * @param updator
     */
    protected void bpUpdate(Updator updator) {
        bpUpdate(updator.getSQL(), updator.getArgs());
    }

    /**
     * Insertor批量插入
     *
     * @param insertorList
     */
    protected void bpInsertBatch(List<Insertor> insertorList) {
        if (null == insertorList) {
            return;
        }

        lock.lock();
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = openSQLiteDatabase();
            sqliteDatabase.beginTransaction();
            for (Insertor insertor : insertorList) {
                debugSql(insertor.getSQL(), insertor.getArgs());
                sqliteDatabase.execSQL(insertor.getSQL(), insertor.getArgs());
            }
            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        } finally {
            if (null != sqliteDatabase) {
                sqliteDatabase.endTransaction();
            }
            closeSQLiteDatabase();
            lock.unlock();
        }
    }

    /**
     * Insertor插入
     *
     * @param insertor
     */
    protected void bpInsert(Insertor insertor) {
        bpUpdate(insertor.getSQL(), insertor.getArgs());
    }

    /**
     * Selector查询
     *
     * @param selector
     * @param multiRowMapper
     * @return
     */
    protected <T> List<T> bpQuery(Selector selector,
                                  MultiRowMapper<T> multiRowMapper) {
        return bpQuery(selector.getSQL(), selector.getArgs(), multiRowMapper);
    }

    /**
     * Selector查询
     *
     * @param selector
     * @param singleRowMapper
     * @return
     */
    protected <T> T bpQuery(Selector selector,
                            SingleRowMapper<T> singleRowMapper) {
        return bpQuery(selector.getSQL(), selector.getArgs(), singleRowMapper);
    }

    /**
     * Selector查询
     *
     * @param selector
     * @param mapRowMapper
     * @return
     */
    protected <K, V> Map<K, V> bpQuery(Selector selector,
                                       MapRowMapper<K, V> mapRowMapper) {
        return bpQuery(selector, mapRowMapper);
    }

    /**
     * 获取数量值
     *
     * @param selector
     * @return
     */
    protected int bpQueryCount(Selector selector) {
        String originSql = selector.getSQL();
        String countSql = originSql.replace("*", "count(*) num");
        return bpQuery(countSql, selector.getArgs(),
                new SingleRowMapper<Integer>() {
                    @Override
                    public Integer mapRow(Cursor rs) throws SQLException {
                        return rs.getInt(rs.getColumnIndex("num"));
                    }
                });
    }

    // ///////////////////////////////利用反射操作数据//////////////////////////////////////

    /**
     * Selector查询
     *
     * @param selector
     * @param clazz
     * @param colume2FieldMapping
     * @return
     */
    protected <T> List<T> bpQueryList(Selector selector, Class<T> clazz,
                                      Map<String, String> colume2FieldMapping) {
        return bpQuery(selector, new DefaultMultiRowMapper<T>(clazz,
                colume2FieldMapping));
    }

    /**
     * Selector查询
     *
     * @param selector
     * @param clazz
     * @param colume2FieldMapping
     * @return
     */
    protected <T> T bpQuerySingle(Selector selector, Class<T> clazz,
                                  Map<String, String> colume2FieldMapping) {
        return bpQuery(selector, new DefaultSingleRowMapper<T>(clazz,
                colume2FieldMapping));
    }

    /**
     * Selector查询
     *
     * @param selector
     * @param clazz
     * @param colume2FieldMapping
     * @param keyName
     * @return
     */
    protected <K, T> Map<String, T> bpQueryMap(Selector selector,
                                               Class<T> clazz, Map<String, String> colume2FieldMapping,
                                               String keyName) {
        return bpQuery(selector, new DefaultMapRowMapper<T>(keyName, clazz,
                colume2FieldMapping));
    }

}
