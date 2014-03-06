package com.example.WiFiInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AMAN
 */
public class FingerprintDS {
    private DatabaseHelper DBHelper; //Not using anymore
    private SQLiteOpenHelper DbHelper;
    private SQLiteDatabase db;

    public static final String KEY_FP_ROWID = "fp_id";
    public static final String KEY_BSSID = "bssid";
    public static final String KEY_SSID = "ssid";
    public static final String KEY_RSS = "rss";
    public static final String KEY_PARK_ROWID = "park_cell_id";                 //Parking Cell id
    public static final String KEY_NAV_ROWID = "nav_cell_id";                 //Navigation Cell id
    public static final String KEY_DIR = "direction";
    public static final String KEY_X_CORD = "x_cord";                        //X Cordinate;
    public static final String KEY_Y_CORD = "y_cord";
    public String ROW_ID = "";

    String tableName = "";
    public String[] Columns;
    public String[] allColumns = {KEY_FP_ROWID, KEY_BSSID, KEY_SSID, KEY_RSS}; //We can remove this later

    public FingerprintDS(Context ctx, String tableName) {

        if (tableName.equals("navigation_table")) {
            DbHelper = new NavCellDatabaseHelper(ctx);
            this.tableName = "navigation_table";
            this.Columns = new String[]{KEY_NAV_ROWID, KEY_FP_ROWID, KEY_DIR, KEY_X_CORD, KEY_Y_CORD};
            this.ROW_ID = KEY_NAV_ROWID;
        }

        if (tableName.equals("parking_table")) {
            DbHelper = new ParkCellDatabaseHelper(ctx);
            this.tableName = "parking_table";
            this.Columns = new String[]{KEY_PARK_ROWID, KEY_NAV_ROWID , KEY_X_CORD, KEY_Y_CORD};
            this.ROW_ID = KEY_PARK_ROWID;
        }


        if (tableName.equals("fingerprint_table")) {
            DbHelper = new DatabaseHelper(ctx);
            this.tableName = "fingerprint_table";
            this.Columns = new String[]{KEY_FP_ROWID, KEY_BSSID, KEY_SSID, KEY_RSS};
            this.ROW_ID = KEY_FP_ROWID;
        }
        //DBHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException //This will open the database
    {
        db = DbHelper.getWritableDatabase();
    }

    public void close()    //This will close the database
    {
        DbHelper.close();
    }

    public boolean deleteTable() //This will delete all rows of any kind of table
    {
        db.rawQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + this.tableName + "'", null);
        return db.delete(this.tableName, null, null) > 0;
    }

    public List<Fingerprint> getAllFingerprints() //Retrieve All the rows
    {
        List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        Cursor cursor = db.query(this.tableName, this.Columns, null, null, null, null, "ssid");  //  Please take a look


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Fingerprint fingerprint = cursorToFingerprint(cursor);
            fingerprints.add(fingerprint);
            cursor.moveToNext();
        }

        cursor.close();
        return fingerprints;
    }


    //---retrieves a particular row---
    public Cursor getRss(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, this.tableName,
                        this.Columns,
                        ROW_ID + "=" + rowId,
                        null,
                        null,
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public HashMap<String, Double> getMeanValue(int scanCount) {
        HashMap<String, Double> hmMean = new HashMap<String, Double>();

        String countQuery = "SELECT BSSID, SSID, AVG(RSS) FROM " + "navigation_table" + " GROUP BY BSSID " +
                "ORDER BY 3 DESC";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            hmMean.put(cursor.getString(0) + " | " + cursor.getString(1), cursor.getDouble(2));
            cursor.moveToNext();
        }
        cursor.close();

        return hmMean;
    }

    public void refreshDB() {
        DBHelper.onUpgrade(db, 1, 2);
    }

    public long insertFingerprint(Fingerprint fp) {        //Insert into Fingerprint Table
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RSS, fp.getRss());
        initialValues.put(KEY_SSID, fp.getSsid());
        initialValues.put(KEY_BSSID, fp.getBssid());

        return db.insert(this.tableName, null, initialValues);
    }

    public long insertParkTable(Fingerprint fp) {         //Insert into Parking Table
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAV_ROWID, fp.getNavid());  // Navigation id "foreign key"
        initialValues.put(KEY_X_CORD, fp.getXCord());
        initialValues.put(KEY_Y_CORD, fp.getYCord());
        return db.insert(tableName, null, initialValues);
    }

    public long insertNavTable(Fingerprint fp) {     //Insert into Navigation Table
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FP_ROWID, fp.getId());     //fp id " foreign key"
        initialValues.put(KEY_DIR, fp.getDirection());
        initialValues.put(KEY_X_CORD, fp.getXCord());
        initialValues.put(KEY_Y_CORD, fp.getYCord());
        return db.insert(tableName, null, initialValues);


    }


    //    Commented for   ---updates a fingerprint---
    /*public boolean updateFingerprint(long rowId, int rss,
                                     String bssid, String ssid, String ref) {
        ContentValues args = new ContentValues();
        args.put(KEY_RSS, rss);
        args.put(KEY_BSSID, bssid);
        args.put(KEY_SSID, ssid);


        return db.update(DatabaseHelper.DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }*/

    public void exportDB() {

    }
    private Fingerprint cursorToFingerprint(Cursor cursor) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setId(cursor.getInt(0));
        fingerprint.setBssid(cursor.getString(1));
        fingerprint.setSsid(cursor.getString(2));
        fingerprint.setRss(cursor.getInt(3));

        return fingerprint;
    }

    private Fingerprint cursorToParkCellTable(Cursor cursor){
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setParkid(cursor.getInt(0));
        fingerprint.setNavid(cursor.getInt(1));
        fingerprint.setXCord(cursor.getInt(2));
        fingerprint.setYCord(cursor.getInt(3));

        return fingerprint;

    }


    private Fingerprint cursorToNavCellTable(Cursor cursor){
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setParkid(cursor.getInt(0));
        fingerprint.setNavid(cursor.getInt(1));
        fingerprint.setXCord(cursor.getInt(2));
        fingerprint.setYCord(cursor.getInt(3));

        return fingerprint;

    }


}
