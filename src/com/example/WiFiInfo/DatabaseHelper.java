package com.example.WiFiInfo;

/**
 * Created by rrajath on 2/21/14.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "fingerprint_db";
    public static final String DATABASE_TABLE = "fingerprint_table";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + "(" +
                    "_id integer primary key autoincrement, " +
                    "bssid text not null, " +
                    "ssid text not null, " +
                    "rss integer not null, " +
                    "refPoint text not null);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
            Log.w(DatabaseHelper.class.getName(),"Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}