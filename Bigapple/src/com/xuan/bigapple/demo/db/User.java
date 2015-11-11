package com.xuan.bigapple.demo.db;

import java.util.Date;

/**
 * 用户实体类
 * 
 * @author xuan
 */
public class User {
	public static final String TABLE_NAME = "user";

	private String id;
	private String name;
	private Date creationTime;

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

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

}
