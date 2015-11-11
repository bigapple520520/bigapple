package com.xuan.bigapple.lib.utils.sharepreference;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.xuan.bigapple.lib.utils.sharepreference.helper.Types;

/**
 * SharedPreference存储的工具类，采用了默认文件存储。<br>
 * 默认文件保存路径：/data/data/<package name>/shared_prefs/<package
 * name>_preferences.xml<br>
 * 该类已单例模式存在，获取单例方式请调用instance方法
 * 
 * 改类废弃，请使用：BPPreferences类，该类优化了可以不用每次都传Context来操作数据，但是前提必须要初始化Biapple.init
 * 
 * @author xuan
 * @version $Revision: 32003 $, $Date: 2012-10-31 14:00:09 +0800 (星期三, 31 十月
 *          2012) $
 */
@Deprecated
public class PreferenceModel {
	private SharedPreferences prefs;
	private Editor prefsEdit;
	private static final PreferenceModel preferenceModel = new PreferenceModel();

	private PreferenceModel() {
	}

	/**
	 * 获取单例
	 * 
	 * @param context
	 * @return
	 */
	public static PreferenceModel instance(Context context) {
		preferenceModel.prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferenceModel;
	}

	// ////////////////////////////////////获取参数部分方法/////////////////////////////////////////////////////
	/**
	 * 获取key对应的value值，值是boolean类型
	 * 
	 * @param key
	 * @param defValue
	 *            当key对应的值不存在时，返回这个默认值
	 * @return
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return (Boolean) getSystemProperties(key, defValue, Types.BOOLEAN);
	}

	/**
	 * 获取key对应的value值，值是float类型
	 * 
	 * @param key
	 * @param defValue
	 *            当key对应的值不存在时，返回这个默认值
	 * @return
	 */
	public float getFloat(String key, float defValue) {
		return (Float) getSystemProperties(key, defValue, Types.FLOAT);
	}

	/**
	 * 获取key对应的value值，值是int类型
	 * 
	 * @param key
	 * @param defValue
	 *            当key对应的值不存在时，返回这个默认值
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return (Integer) getSystemProperties(key, defValue, Types.INTEGER);
	}

	/**
	 * 获取key对应的value值，值是long类型
	 * 
	 * @param key
	 * @param defValue
	 *            当key对应的值不存在时，返回这个默认值
	 * @return
	 */
	public long getLong(String key, long defValue) {
		return (Long) getSystemProperties(key, defValue, Types.LONG);
	}

	/**
	 * 获取key对应的value值，值是String类型
	 * 
	 * @param key
	 * @param defValue
	 *            当key对应的值不存在时，返回这个默认值
	 * @return
	 */
	public String getString(String key, String defValue) {
		return (String) getSystemProperties(key, defValue, Types.STRING);
	}

	/**
	 * 根据key得到properties配置
	 * 
	 * @param key
	 *            唯一key值
	 * @param defValue
	 *            默认值，当key对应的值不存在时，就返回这个默认值
	 * @param type
	 *            获取的值的类型，详情参看：Types枚举
	 * @return
	 */
	public Object getSystemProperties(String key, Object defValue, Types type) {
		Object curValue = null;
		switch (type) {
		case BOOLEAN:
			curValue = prefs.getBoolean(key, (Boolean) defValue);
			break;
		case FLOAT:
			curValue = prefs.getFloat(key, (Float) defValue);
			break;
		case INTEGER:
			curValue = prefs.getInt(key, (Integer) defValue);
			break;
		case LONG:
			curValue = prefs.getLong(key, (Long) defValue);
			break;
		case STRING:
			curValue = prefs.getString(key, (String) defValue);
			break;
		}

		return curValue;
	}

	/**
	 * 获取所有存贮参数的Map集合
	 * 
	 * @return
	 */
	public Map<String, ?> getAllSystemProperties() {
		return prefs.getAll();
	}

	// ////////////////////////////////////保存参数方法部分////////////////////////////////////////////////////////////
	/**
	 * 以<key,value>的方式保存参数，value是boolean类型
	 * 
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, boolean value) {
		saveSystemProperties(key, value, Types.BOOLEAN);
	}

	/**
	 * 以<key,value>的方式保存参数，value是float类型
	 * 
	 * @param key
	 * @param value
	 */
	public void putFloat(String key, float value) {
		saveSystemProperties(key, value, Types.FLOAT);
	}

	/**
	 * 以<key,value>的方式保存参数，value是int类型
	 * 
	 * @param key
	 * @param value
	 */
	public void putInt(String key, int value) {
		saveSystemProperties(key, value, Types.INTEGER);
	}

	/**
	 * 以<key,value>的方式保存参数，value是long类型
	 * 
	 * @param key
	 * @param value
	 */
	public void putLong(String key, long value) {
		saveSystemProperties(key, value, Types.LONG);
	}

	/**
	 * 以<key,value>的方式保存参数，value是String类型
	 * 
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value) {
		saveSystemProperties(key, value, Types.STRING);
	}

	/**
	 * 以<key,value>的方式保存参数
	 * 
	 * @param key
	 *            唯一key值
	 * @param value
	 *            参数的值
	 * @param type
	 *            参数值的类型，对应支持的类型参看：Types枚举
	 */
	public void saveSystemProperties(String key, Object value, Types type) {
		if (prefsEdit == null) {
			initPrefsEdit();
		}

		switch (type) {
		case BOOLEAN:
			prefsEdit.putBoolean(key, (Boolean) value);
			break;
		case FLOAT:
			prefsEdit.putFloat(key, (Float) value);
			break;
		case INTEGER:
			prefsEdit.putInt(key, (Integer) value);
			break;
		case LONG:
			prefsEdit.putLong(key, (Long) value);
			break;
		case STRING:
			prefsEdit.putString(key, (String) value);
			break;
		}

		commitPrefsEdit();
	}

	// /////////////////////////////////////////////删除参数方法部分///////////////////////////////////////////////////////
	/**
	 * 删除对应key的参数
	 * 
	 * @param key
	 *            唯一key值
	 */
	public void removeSystemProperties(String key) {
		if (null == prefsEdit) {
			initPrefsEdit();
		}

		prefsEdit.remove(key);
		commitPrefsEdit();
	}

	/**
	 * 获取原始的SharedPreferences对象，当封装的方法不够用时，可以用这个对象来操作更丰富的API
	 * 
	 * @return
	 */
	public SharedPreferences getDefaultSharedPreferences() {
		return prefs;
	}

	// 为减少创建开销，只有当需要保存参数的时候才初始化prefsEdit
	private synchronized void initPrefsEdit() {
		if (null == prefsEdit) {
			prefsEdit = prefs.edit();
		}
	}

	// 保存编辑提交
	private void commitPrefsEdit() {
		if (null != prefsEdit) {
			prefsEdit.commit();
		}
	}

}
