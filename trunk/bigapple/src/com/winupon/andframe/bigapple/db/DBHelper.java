package com.winupon.andframe.bigapple.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

/**
 * DBHelper，第一次运行程序或升级程序后自动创建或升级数据库
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-20 下午7:33:06 $
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "bigapple.DBHelper";

    // 用于初始化或升级数据库的文件名，每升一次db.version就+1
    private static final String DB_INIT_OR_UPGRADE_FILENAME = "db_${db.version}.sql";

    // 数据库版本号，程序初始化时必须初始化此值
    private static int DATABASE_VERSION = -1;

    // 数据库名，使用时可以根据自己项目定义名称
    public static String DATABASE_NAME = "bigapple";

    private final Context context;

    /**
     * 设置数据库版本号，必须初始化，默认的数据库名师bigapple
     * 
     * @param DATABASE_VERSION
     *            the DATABASE_VERSION to set
     */
    public static void init(int DATABASE_VERSION) {
        DBHelper.DATABASE_VERSION = DATABASE_VERSION;
    }

    /**
     * 设置数据库版本号，必须初始化
     * 
     * @param DATABASE_VERSION
     * @param DATABASE_NAME
     */
    public static void init(int DATABASE_VERSION, String DATABASE_NAME) {
        DBHelper.DATABASE_VERSION = DATABASE_VERSION;
        DBHelper.DATABASE_NAME = DATABASE_NAME;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * 数据库第一次被创建时被调用（不在构造函数中发生，是在调用getWritableDatabase或getReadableDatabase时被调用时）
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DATABASE_VERSION <= 0) {
            throw new RuntimeException("DBHelper.DATABASE_VERSION必须初始化，请调用init方法");
        }
        Log.i(TAG, "initing dababase");

        // read and execute assets/db_1.sql
        executeSqlFromFile(db, DB_INIT_OR_UPGRADE_FILENAME.replace("${db.version}", "1"));

        // 如果调用onCreate时版本号比1大，则升级(发生在多次升级后，用户第一次安装，或者用户卸载后重新安装)
        if (DATABASE_VERSION > 1) {
            onUpgrade(db, 1, DATABASE_VERSION);
        }
    }

    /**
     * 数据库的版本号表明要升级时被调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DATABASE_VERSION <= 0) {
            throw new RuntimeException("DBHelper.DATABASE_VERSION必须初始化，请调用init方法");
        }

        if (newVersion <= oldVersion) {
            return;
        }

        Log.i(TAG, "updating dababase from version " + oldVersion + "to version " + newVersion);
        for (int i = oldVersion + 1; i <= newVersion; i++) {
            executeSqlFromFile(db, DB_INIT_OR_UPGRADE_FILENAME.replace("${db.version}", String.valueOf(i)));
        }
    }

    /**
     * 从文件读取sql并执行
     * 
     * @param db
     * @param fileName
     */
    private void executeSqlFromFile(SQLiteDatabase db, String fileName) {
        Log.i(TAG, "begin to execute sql in assets/" + fileName);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));

            String line = null;
            StringBuilder sql = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--")) {// 注释行
                    continue;
                }
                if (line.trim().equalsIgnoreCase("go")) {// 表示一句sql的结束
                    if (!TextUtils.isEmpty(sql.toString())) {
                        // 执行sql
                        db.execSQL(sql.toString());
                    }
                    sql = new StringBuilder();
                    continue;
                }
                sql.append(line);
            }
            if (!TextUtils.isEmpty(sql.toString())) {
                // 执行sql
                db.execSQL(sql.toString());
            }

        }
        catch (Exception e) {
            Log.e(TAG, "", e);
            throw new RuntimeException(e);
        }

        Log.i(TAG, "succeed to execute sql in assets/" + fileName);
    }

}
