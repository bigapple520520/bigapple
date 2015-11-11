package com.xuan.bigapple.demo.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.xuan.bigapple.lib.db.BPBaseDao;
import com.xuan.bigapple.lib.db.sqlmarker.Deletor;
import com.xuan.bigapple.lib.db.sqlmarker.Insertor;
import com.xuan.bigapple.lib.db.sqlmarker.Selector;
import com.xuan.bigapple.lib.utils.DateUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;
import com.xuan.bigapple.lib.utils.uuid.UUIDUtils;

/**
 * 可保持单例的数据库操作
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-8 下午2:25:17 $
 */
public class UserDao extends BPBaseDao {
	private static final HashMap<String, String> MAPPING = new HashMap<String, String>();
	static {
		MAPPING.put("id", "id");
		MAPPING.put("name", "name");
		MAPPING.put("creation_time", "creationTime");
	}

	/**
	 * 单条插入数据
	 * 
	 * @param name
	 */
	public void insertTest(String name) {
		bpInsert(Insertor
				.insertInto(User.TABLE_NAME)
				.value("id", UUIDUtils.createId())
				.value("name", name)
				.value("creation_time",
						DateUtils.date2StringBySecond(new Date())));
	}

	public void deleteTest(String name) {
		bpDetele(Deletor.deleteFrom(User.TABLE_NAME).where("name=?", name));
	}

	public void findTest() {
		List<User> userList = bpQueryList(Selector.from(User.TABLE_NAME),
				User.class, MAPPING);
		LogUtils.e(userList.size() + "");
	}

	// /**
	// * 批量插入
	// */
	// public void insertBatchTest() {
	// List<ContentValues> valuesList = new ArrayList<ContentValues>();
	// for (int i = 0; i < 1000; i++) {
	// ContentValues values = new ContentValues();
	// values.put("id", UUIDUtils.createId());
	// values.put("name", "name_" + UUIDUtils.createId());
	// values.put("creation_time",
	// DateUtils.date2StringBySecond(new Date()));
	// valuesList.add(values);
	// }
	//
	// insertBatch(User.TABLE_NAME, null, valuesList);
	// }
	//
	// /**
	// * 删除所有数据
	// */
	// public void deleteTest() {
	// delete(User.TABLE_NAME, null, null);
	// }
	//
	// /**
	// * 查找所有的数据
	// *
	// * @return
	// */
	// public List<User> findAllUser() {
	// return query(User.TABLE_NAME, null, null, null, null, null, null,
	// new MMultiRowMapper());
	// }
	//
	// /**
	// * 返回的结果集处理
	// *
	// * @author xuan
	// */
	// private class MMultiRowMapper implements MultiRowMapper<User> {
	// @Override
	// public User mapRow(Cursor cs, int rowNum) throws SQLException {
	// User user = new User();
	// user.setId(cs.getString(cs.getColumnIndex("id")));
	// user.setName(cs.getString(cs.getColumnIndex("name")));
	// user.setCretaionTime(DateUtils.string2DateTime(cs.getString(cs
	// .getColumnIndex("creation_time"))));
	// return user;
	// }
	// }

}
