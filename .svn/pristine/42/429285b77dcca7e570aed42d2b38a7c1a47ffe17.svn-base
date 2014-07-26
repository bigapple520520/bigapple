/* 
 * @(#)TestDaoAdapter.java    Created on 2013-11-8
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.db;

import android.content.ContentValues;
import android.content.Context;

import com.winupon.andframe.bigapple.db.BasicDaoAdapter;
import com.winupon.andframe.bigapple.utils.uuid.UUIDUtils;

/**
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-8 下午2:28:18 $
 */
public class TestDaoAdapter extends BasicDaoAdapter {
    public TestDaoAdapter(Context context) {
        super(context);
    }

    public void insertTest(String name) {
        ContentValues cv = new ContentValues();
        cv.put("id", UUIDUtils.createId());
        cv.put("name", name);

        getSQLiteDatabase().insert("test_table", null, cv);
    }

    public void insertBatchTest() {

        getSQLiteDatabase().beginTransaction();
        for (int i = 0; i < 100; i++) {
            ContentValues cv = new ContentValues();
            cv.put("id", UUIDUtils.createId());
            cv.put("name", String.valueOf(i));

            getSQLiteDatabase().insert("test_table", null, cv);
        }
        getSQLiteDatabase().setTransactionSuccessful();
        getSQLiteDatabase().endTransaction();
    }

}
