package com.winupon.andframe.bigapple.db.helper;

/**
 * 对象属性的键值对
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-8-18 下午4:10:58 $
 */
public class KeyValue {
    public final String key;
    public final Object value;

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
