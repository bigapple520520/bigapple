/* 
 * @(#)DaoFactory.java    Created on 2014-3-21
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.db;

/**
 * dao工厂类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-21 下午4:37:55 $
 */
public abstract class DaoFactory {

    private final static TestDao2 testDao2 = new TestDao2();

    public static TestDao2 getTestDao2() {
        return testDao2;
    }

}
