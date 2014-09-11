package com.winupon.andframe.zzdemo.db.entity;

import java.util.Date;

/**
 * 用户实体类
 * 
 * @author xuan
 */
public class User {
    private String id;
    private String name;
    private Date cretaionTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCretaionTime() {
        return cretaionTime;
    }

    public void setCretaionTime(Date cretaionTime) {
        this.cretaionTime = cretaionTime;
    }

}
