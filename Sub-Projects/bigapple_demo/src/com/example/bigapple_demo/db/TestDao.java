/* 
 * @(#)TestDao.java    Created on 2013-11-8
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.winupon.andframe.bigapple.db.BasicDao;
import com.winupon.andframe.bigapple.utils.uuid.UUIDUtils;

/**
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-8 下午2:25:17 $
 */
public class TestDao extends BasicDao {
    public TestDao(Context context) {
        super(context);
    }

    public void insertTest(String name) {
        update("insert into test_table(id,name) values(?,?)", new String[] { UUIDUtils.createId(), name });
    }

    public void insertBatchTest() {
        List<Object[]> data = new ArrayList<Object[]>();

        for (int i = 0; i < 100; i++) {
            Object[] d = new Object[2];
            d[0] = UUIDUtils.createId();
            d[1] = String.valueOf(i);
            data.add(d);
        }

        updateBatch("insert into test_table(id,name) values(?,?)", data);
    }

}
