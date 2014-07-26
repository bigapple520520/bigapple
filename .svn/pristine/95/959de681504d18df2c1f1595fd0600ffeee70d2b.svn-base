/* 
 * @(#)PreferenceModel.java    Created on 2011-12-20
 * Copyright (c) 2011 ZDSoft Networks, Inc. All rights reserved.
 * $Id: PreferenceModel.java 32003 2012-10-31 06:00:09Z xuan $
 */
package com.winupon.andframe.bigapple.utils.sharepreference;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.winupon.andframe.bigapple.utils.sharepreference.helper.Types;

/**
 * SharedPreference存储的工具类，采用了默认文件存储。<br>
 * 默认文件保存路径：/data/data/<package name>/shared_prefs/<package name>_preferences.xml
 * 
 * @author xuan
 * @version $Revision: 32003 $, $Date: 2012-10-31 14:00:09 +0800 (星期三, 31 十月 2012) $
 */
public class PreferenceModel {
    private SharedPreferences prefs;
    private Editor prefsEdit;
    private static final PreferenceModel preferenceModel = new PreferenceModel();

    private PreferenceModel() {
    }

    public static PreferenceModel instance(Context context) {
        preferenceModel.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return preferenceModel;
    }

    // ////////////////////////////////////获取参数部分方法/////////////////////////////////////////////////////
    public boolean getBoolean(String key, boolean defValue) {
        return (Boolean) getSystemProperties(key, defValue, Types.BOOLEAN);
    }

    public float getFloat(String key, float defValue) {
        return (Float) getSystemProperties(key, defValue, Types.FLOAT);
    }

    public int getInt(String key, int defValue) {
        return (Integer) getSystemProperties(key, defValue, Types.INTEGER);
    }

    public long getLong(String key, long defValue) {
        return (Long) getSystemProperties(key, defValue, Types.LONG);
    }

    public String getString(String key, String defValue) {
        return (String) getSystemProperties(key, defValue, Types.STRING);
    }

    /**
     * 根据key得到properties配置
     * 
     * @param key
     * @param defValue
     * @param type
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
     * 获取所有参数的集合
     * 
     * @return
     */
    public Map<String, ?> getAllSystemProperties() {
        return prefs.getAll();
    }

    // ////////////////////////////////////保存参数方法部分////////////////////////////////////////////////////////////
    public void putBoolean(String key, boolean value) {
        saveSystemProperties(key, value, Types.BOOLEAN);
    }

    public void putFloat(String key, float value) {
        saveSystemProperties(key, value, Types.FLOAT);
    }

    public void putInt(String key, int value) {
        saveSystemProperties(key, value, Types.INTEGER);
    }

    public void putLong(String key, long value) {
        saveSystemProperties(key, value, Types.LONG);
    }

    public void putString(String key, String value) {
        saveSystemProperties(key, value, Types.STRING);
    }

    /**
     * 保存properties配置
     * 
     * @param key
     * @param value
     * @param type
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
     * 删除对应key的properties参数配置
     * 
     * @param key
     */
    public void removeSystemProperties(String key) {
        if (prefsEdit == null) {
            initPrefsEdit();
        }

        prefsEdit.remove(key);
        commitPrefsEdit();
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return prefs;
    }

    // 为减少创建开销，只有当需要保存参数的时候才初始化prefsEdit
    private synchronized void initPrefsEdit() {
        if (prefsEdit == null) {
            prefsEdit = prefs.edit();
        }
    }

    // 保存编辑提交
    private void commitPrefsEdit() {
        if (prefsEdit != null) {
            prefsEdit.commit();
        }
    }

}
