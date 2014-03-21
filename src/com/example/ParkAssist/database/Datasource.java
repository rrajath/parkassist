package com.example.ParkAssist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ParkAssist.entity.Cell;
import com.example.ParkAssist.entity.Fingerprint;
import com.example.ParkAssist.entity.NavCell;
import com.example.ParkAssist.entity.ParkCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AMAN
 */
public class Datasource {
    private SQLiteOpenHelper DbHelper;
    private SQLiteDatabase db;

    public static final String KEY_FP_ROWID = "fp_id";              // Fingerprint ID
    public static final String KEY_BSSID = "bssid";                 // BSSID of the access point
    public static final String KEY_SSID = "ssid";                   // SSID of the access point
    public static final String KEY_RSS = "rss";                     // Signal Strength value

    public static final String KEY_PARK_ROWID = "park_cell_id";     // Parking Cell id
    public static final String KEY_NAV_ROWID = "nav_cell_id";       // Navigation Cell id
    public static final String KEY_DIR = "direction";               // Nav path direction
    public static final String KEY_X_CORD = "x_cord";               // X Coordinate
    public static final String KEY_Y_CORD = "y_cord";               // Y Coordinate
    public String rowId = "";

    String tableName = "";
    public String[] columns;

    public Datasource(Context ctx, String tableName) {

        if (tableName.equals("navigation_table")) {
            DbHelper = new NavCellDBHelper(ctx);
            this.tableName = "navigation_table";
            this.columns = new String[]{KEY_NAV_ROWID, KEY_FP_ROWID, KEY_DIR, KEY_X_CORD, KEY_Y_CORD};
            this.rowId = KEY_NAV_ROWID;
        }

        if (tableName.equals("parking_table")) {
            DbHelper = new ParkCellDBHelper(ctx);
            this.tableName = "parking_table";
            this.columns = new String[]{KEY_PARK_ROWID, KEY_NAV_ROWID, KEY_X_CORD, KEY_Y_CORD};
            this.rowId = KEY_PARK_ROWID;
        }


        if (tableName.equals("fingerprint_table")) {
            DbHelper = new FingerprintDBHelper(ctx);
            this.tableName = "fingerprint_table";
            this.columns = new String[]{KEY_FP_ROWID, KEY_BSSID, KEY_SSID, KEY_RSS};
            this.rowId = KEY_FP_ROWID;
        }
    }

    /*
     * Opens database connection
     */
    public void open() throws SQLException
    {
        db = DbHelper.getWritableDatabase();
    }

    /*
     * Closes database connection
     */
    public void close()
    {
        DbHelper.close();
    }

    /*
     * Deletes all rows from the table
     */
    public void deleteSequence(String tableName)
    {
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME='" + tableName + "'");
    }

    public boolean deleteTable(String tableName) {
        return db.delete(tableName, null, null) > 0;
    }

    public List<Fingerprint> getAllFingerprints() //Retrieve All the rows
    {
        List<Fingerprint> fingerprints = new ArrayList<Fingerprint>();
        Cursor cursor = db.query(this.tableName, this.columns, null, null, null, null, "ssid");  //  Please take a look


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Fingerprint fingerprint = cursorToFingerprint(cursor);
            fingerprints.add(fingerprint);
            cursor.moveToNext();
        }

        cursor.close();
        return fingerprints;
    }

    public List getAllRows() {
        List objects = new ArrayList();
        Cursor cursor = db.query(this.tableName, this.columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (tableName.equals("fingerprint_table")) {
                Fingerprint fingerprint = cursorToFingerprint(cursor);
                objects.add(fingerprint);
                cursor.moveToNext();
            } else if (tableName.equals("parking_table")) {
                ParkCell parkCell = cursorToParkCell(cursor);
                objects.add(parkCell);
                cursor.moveToNext();
            } else {
                NavCell navCell = cursorToNavCell(cursor);
                objects.add(navCell);
                cursor.moveToNext();
            }

        }
        cursor.close();
        return objects;
    }

    //---retrieves a particular row---
    public Cursor getRss(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, this.tableName,
                        this.columns,
                        this.rowId + "=" + rowId,
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

    public void refreshDB() {
        DbHelper.onUpgrade(db, 1, 2);
    }

    public long insertFingerprint(Fingerprint fp) {        //Insert into Fingerprint Table
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RSS, fp.getRss());
        initialValues.put(KEY_SSID, fp.getSsid());
        initialValues.put(KEY_BSSID, fp.getBssid());

        return db.insert(this.tableName, null, initialValues);
    }

    // Insert into ParkCell Table
    public long insertParkCell(ParkCell parkCell) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAV_ROWID, parkCell.getNavCellId());  // Navigation id "foreign key"
        initialValues.put(KEY_X_CORD, parkCell.getXCord());
        initialValues.put(KEY_Y_CORD, parkCell.getYCord());

        return db.insert(tableName, null, initialValues);
    }

    // Insert into NavCell Table
    public long insertNavCell(NavCell navCell) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FP_ROWID, navCell.getNavCellId());     //fp id " foreign key"
        initialValues.put(KEY_DIR, navCell.getDirection());
        initialValues.put(KEY_X_CORD, navCell.getXCord());
        initialValues.put(KEY_Y_CORD, navCell.getYCord());
        return db.insert(tableName, null, initialValues);
    }

    public int updateParkCell(int x, int y, int navCellId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("nav_cell_id", navCellId);
        String whereClause = KEY_X_CORD + " = " + x + " AND " + KEY_Y_CORD + " = " + y;
        return db.update(tableName, contentValues, whereClause, null);
    }

    public Cell getCell(int x, int y) {
        String whereClause = " X_CORD = " + x + " AND Y_CORD = " + y;
        Cursor cursor = db.query(this.tableName, this.columns, whereClause, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Cell cell;
        if (tableName.equals("parking_table")) {
            cell = (ParkCell)cursorToParkCell(cursor);
        } else {
            cell = (NavCell)cursorToNavCell(cursor);
        }

        return cell;
    }

    private Fingerprint cursorToFingerprint(Cursor cursor) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setFpId(cursor.getInt(0));
        fingerprint.setBssid(cursor.getString(1));
        fingerprint.setSsid(cursor.getString(2));
        fingerprint.setRss(cursor.getInt(3));

        return fingerprint;
    }

    private ParkCell cursorToParkCell(Cursor cursor) {
        ParkCell parkCell = new ParkCell();
        parkCell.setParkCellId(cursor.getInt(0));
        parkCell.setNavCellId(cursor.getInt(1));
        parkCell.setXCord(cursor.getInt(2));
        parkCell.setYCord(cursor.getInt(3));

        return parkCell;
    }

    private NavCell cursorToNavCell(Cursor cursor) {
        if (cursor.getCount() <= 0) {
            return null;
        }
        NavCell navCell = new NavCell();
        navCell.setNavCellId(cursor.getInt(0));
        navCell.setFpId(cursor.getInt(1));
        navCell.setDirection(cursor.getString(2));
        navCell.setXCord(cursor.getInt(3));
        navCell.setYCord(cursor.getInt(4));

        return navCell;
    }
}