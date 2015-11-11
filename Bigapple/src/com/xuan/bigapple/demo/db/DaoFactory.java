package com.xuan.bigapple.demo.db;

/**
 * dao工厂类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-3-21 下午4:37:55 $
 */
public abstract class DaoFactory {
	private final static UserDao userDao = new UserDao();

	public static UserDao getUserdao() {
		return userDao;
	}

}
