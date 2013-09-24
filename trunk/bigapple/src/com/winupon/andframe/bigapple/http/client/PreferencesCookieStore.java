package com.winupon.andframe.bigapple.http.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * CookieManager将调用 CookieStore.add为每个传入的 HTTP响应保存cookie。<br>
 * 调用 CookieStore.get为每个传出的 HTTP请求获取 cookie。CookieStore负责移除已过期的 HttpCookie实例。<br>
 * 
 * 下面的实现是将CookieStore保存在Preferences里面
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-2 下午7:49:03 $
 */
public class PreferencesCookieStore implements CookieStore {
    private static final String COOKIE_PREFS = "CookiePrefsFile";// 保存Cookie文件的key
    private static final String COOKIE_NAME_STORE = "names";// 所有Cookie的name，用逗号分开
    private static final String COOKIE_NAME_PREFIX = "cookie_";// Cookie名字的前缀

    private final ConcurrentHashMap<String, Cookie> cookies;
    private final SharedPreferences cookiePrefs;

    public PreferencesCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        cookies = new ConcurrentHashMap<String, Cookie>();

        // 加载所有保存在配置文件中的Cookie
        String storedCookieNames = cookiePrefs.getString(COOKIE_NAME_STORE, null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames) {
                String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        cookies.put(name, decodedCookie);
                    }
                }
            }

            // 清理过期的Cookie
            clearExpired(new Date());
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        String name = cookie.getName();

        // 如果传入的Cookie没有过期，就新加，如果过期的就删除
        if (!cookie.isExpired(new Date())) {
            cookies.put(name, cookie);
        }
        else {
            cookies.remove(name);
        }

        // 把Cookie保存到文件中
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        prefsWriter.commit();
    }

    @Override
    public void clear() {
        // 清理包括在内存中或者文件中的所有Cookie
        cookies.clear();
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        for (String name : cookies.keySet()) {
            prefsWriter.remove(COOKIE_NAME_PREFIX + name);
        }
        prefsWriter.remove(COOKIE_NAME_STORE);
        prefsWriter.commit();
    }

    @Override
    public boolean clearExpired(Date date) {
        boolean clearedAny = false;// 判断是否有至少一个Cookie被清理了
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

        for (ConcurrentHashMap.Entry<String, Cookie> entry : cookies.entrySet()) {
            String name = entry.getKey();
            Cookie cookie = entry.getValue();
            if (cookie.isExpired(date)) {
                // 清除cookies
                cookies.remove(name);

                // 删除文件中的Cookie
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);

                clearedAny = true;
            }
        }

        // 如果有Cookie被清理了，就更新保存所有Cookie名字的那个字段
        if (clearedAny) {
            prefsWriter.putString(COOKIE_NAME_STORE, TextUtils.join(",", cookies.keySet()));
        }
        prefsWriter.commit();

        return clearedAny;
    }

    @Override
    public List<Cookie> getCookies() {
        return new ArrayList<Cookie>(cookies.values());
    }

    /**
     * 加密Cookie成字符串
     * 
     * @param cookie
     * @return
     */
    protected String encodeCookie(SerializableCookie cookie) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        }
        catch (Exception e) {
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * 解密字符串成Cookie
     * 
     * @param cookieStr
     * @return
     */
    protected Cookie decodeCookie(String cookieStr) {
        byte[] bytes = hexStringToByteArray(cookieStr);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            cookie = ((SerializableCookie) ois.readObject()).getCookie();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return cookie;
    }

    // 加密用，其中算法，如果你喜欢可以自己复写这个方法
    protected String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (byte element : b) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    // 解密用
    protected byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 可序列化Cookie
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-8-2 下午7:51:16 $
     */
    public class SerializableCookie implements Serializable {
        private static final long serialVersionUID = 6374381828722046732L;

        private transient final Cookie cookie;
        private transient BasicClientCookie clientCookie;

        public SerializableCookie(Cookie cookie) {
            this.cookie = cookie;
        }

        public Cookie getCookie() {
            Cookie bestCookie = cookie;
            if (clientCookie != null) {
                bestCookie = clientCookie;
            }
            return bestCookie;
        }

        // 序列化写入
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(cookie.getName());
            out.writeObject(cookie.getValue());
            out.writeObject(cookie.getComment());
            out.writeObject(cookie.getDomain());
            out.writeObject(cookie.getExpiryDate());
            out.writeObject(cookie.getPath());
            out.writeInt(cookie.getVersion());
            out.writeBoolean(cookie.isSecure());
        }

        // 序列化读出
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            String name = (String) in.readObject();
            String value = (String) in.readObject();
            clientCookie = new BasicClientCookie(name, value);
            clientCookie.setComment((String) in.readObject());
            clientCookie.setDomain((String) in.readObject());
            clientCookie.setExpiryDate((Date) in.readObject());
            clientCookie.setPath((String) in.readObject());
            clientCookie.setVersion(in.readInt());
            clientCookie.setSecure(in.readBoolean());
        }
    }

}
