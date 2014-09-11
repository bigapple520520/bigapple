/* 
 * @(#)LoginUser.java    Created on 2014-8-18
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.db.entity;

import java.util.Date;

import com.winupon.andframe.bigapple.utils.uuid.UUIDUtils;

/**
 * 登录用户
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-18 下午4:59:49 $
 */
public class LoginUser {
    private String region_id = UUIDUtils.createId();
    private String username = "xuan";
    private String password = "123456";
    private int auto_login = 1;
    private Date creation_time = new Date();

    // dto
    private String name = "徐安";

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAuto_login() {
        return auto_login;
    }

    public void setAuto_login(int auto_login) {
        this.auto_login = auto_login;
    }

    public Date getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(Date creation_time) {
        this.creation_time = creation_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
