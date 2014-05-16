/* 
 * @(#)TestDaoAdapter.java    Created on 2014-3-18
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.db;

import android.content.ContentValues;
import android.content.Context;

import com.winupon.andframe.bigapple.db.BasicDaoAdapter;
import com.winupon.andframe.bigapple.utils.uuid.UUIDUtils;

/**
 * db部分demo的dao
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-18 下午12:58:13 $
 */
public class TestDaoAdapter extends BasicDaoAdapter {
    public TestDaoAdapter(Context context) {
        super(context);
    }

    /**
     * 单条插入数据
     * 
     * @param name
     */
    public void insertTest(String name) {
        ContentValues values = new ContentValues();
        values.put("id", UUIDUtils.createId());
        values.put("name", name);
        getSQLiteDatabase().insert("test_table", null, values);
    }

}
