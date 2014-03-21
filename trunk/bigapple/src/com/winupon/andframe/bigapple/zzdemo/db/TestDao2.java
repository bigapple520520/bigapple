/* 
 * @(#)TestDao.java    Created on 2013-11-8
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.zzdemo.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;

import com.winupon.andframe.bigapple.db.BasicDao2;
import com.winupon.andframe.bigapple.db.callback.MultiRowMapper;
import com.winupon.andframe.bigapple.utils.DateUtils;
import com.winupon.andframe.bigapple.utils.uuid.UUIDUtils;

/**
 * 可保持单例的数据库操作
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-8 下午2:25:17 $
 */
public class TestDao2 extends BasicDao2 {
    private final static String SQL_INSERT = "INSERT INTO test_table(id,name,creation_time) VALUES(?,?,?)";
    private final static String SQL_DELETE = "DELETE FROM test_table";
    private final static String SQL_FIND = "SELECT * FROM test_table";

    /**
     * 单条插入数据
     * 
     * @param name
     */
    public void insertTest(String name) {
        update(SQL_INSERT, new String[] { UUIDUtils.createId(), name, DateUtils.date2StringBySecond(new Date()) });
    }

    /**
     * 批量插入
     */
    public void insertBatchTest() {
        List<Object[]> data = new ArrayList<Object[]>();

        for (int i = 0; i < 1000; i++) {
            Object[] d = new Object[2];
            d[0] = UUIDUtils.createId();
            d[1] = String.valueOf(i);
            d[2] = DateUtils.date2StringBySecond(new Date());
            data.add(d);
        }

        updateBatch(SQL_INSERT, data);
    }

    /**
     * 删除所有数据
     */
    public void deleteTest() {
        update(SQL_DELETE);
    }

    /**
     * 查找所有的数据
     * 
     * @return
     */
    public List<User> findAllUser() {
        return query(SQL_FIND, new String[] {}, new MMultiRowMapper());
    }

    /**
     * 返回的结果集处理
     * 
     * @author xuan
     */
    private class MMultiRowMapper implements MultiRowMapper<User> {
        @Override
        public User mapRow(Cursor cs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(cs.getString(cs.getColumnIndex("id")));
            user.setName(cs.getString(cs.getColumnIndex("name")));
            user.setCretaionTime(DateUtils.string2DateTime(cs.getString(cs.getColumnIndex("creation_time"))));
            return user;
        }
    }

}
