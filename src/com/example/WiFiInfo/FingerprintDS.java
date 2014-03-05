package com.example.WiFiInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rrajath on 2/21/14.
 */
public class FingerprintDS {
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_BSSID = "bssid";
    public static final String KEY_SSID = "ssid";
    public static final String KEY_RSS = "rss";
    public static final String KEY_REF = "refPoint";

    public String[] allColumns = {KEY_ROWID, KEY_BSSID, KEY_SSID, KEY_RSS,KEY_REF };

    public FingerprintDS(Context ctx) {
        DBHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException //This will open the database
    {
        db = DBHelper.getWritableDatabase();
    }

    public void close()    //This will close the database
    {
        DBHelper.close();
    }

    public boolean deleteFingerprint() //This will delete all rows
    {
        db.rawQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + DatabaseHelper.DATABASE_TABLE + "'", null);
        return db.delete(DatabaseHelper.DATABASE_TABLE, null, null) > 0;
    }

    public List<Fingerprint> getAllFingerprints() //Retrieve All the rows
    {
        List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        Cursor cursor = db.query(DatabaseHelper.DATABASE_TABLE, allColumns, null, null, null, null, "ssid");

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
                db.query(true, DatabaseHelper.DATABASE_TABLE,
                        allColumns,
                        KEY_ROWID + "=" + rowId,
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

        String countQuery = "SELECT BSSID, SSID, AVG(RSS) FROM " + DatabaseHelper.DATABASE_TABLE + " GROUP BY BSSID " +
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

    public long insertFingerprint(Fingerprint fp) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RSS, fp.getRss());
        initialValues.put(KEY_SSID, fp.getSsid());
        initialValues.put(KEY_BSSID, fp.getBssid());
        initialValues.put(KEY_REF,fp.getRefPoint());

        return db.insert(DatabaseHelper.DATABASE_TABLE, null, initialValues);
    }


    //---updates a fingerprint---
    public boolean updateFingerprint(long rowId, int rss,
                                     String bssid, String ssid ,String ref) {
        ContentValues args = new ContentValues();
        args.put(KEY_RSS, rss);
        args.put(KEY_BSSID, bssid);
        args.put(KEY_SSID, ssid);
        args.put(KEY_REF,ref);


        return db.update(DatabaseHelper.DATABASE_TABLE, args,
                KEY_ROWID + "=" + rowId, null) > 0;
    }

    public void exportDB(){

    }

    private Fingerprint cursorToFingerprint(Cursor cursor) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setId(cursor.getInt(0));
        fingerprint.setBssid(cursor.getString(1));
        fingerprint.setSsid(cursor.getString(2));
        fingerprint.setRss(cursor.getInt(3));
        fingerprint.setRefPoint(cursor.getString(4));

        return fingerprint;
    }
}
