package com.example.ParkAssist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by AMAN on 3/5/14.
 */
public class ParkCellDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_TABLE = " parking_table";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PARK_CREATE =
            "create table" + DATABASE_TABLE + "(" +
                    "park_cell_id integer primary key autoincrement," +
                    "nav_cell_id  integer not null," +
                    "x_cord  integer not null , " +
                    "y_cord integer not null," +
                    "FOREIGN KEY(nav_cell_id) REFERENCES navigation_table(nav_cell_id));";


    ParkCellDBHelper(Context context) {

        super(context, FingerprintDBHelper.DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_PARK_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        Log.w(FingerprintDBHelper.class.getName(), "Upgrading database from version " + oldVersion
                + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }


}
