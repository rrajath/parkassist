package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import com.example.ParkAssist.R;
import com.example.ParkAssist.database.Datasource;
import com.example.ParkAssist.entity.Cell;
import com.example.ParkAssist.entity.Fingerprint;
import com.example.ParkAssist.entity.NavCell;
import com.example.ParkAssist.util.OfflineScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanActivity extends Activity {
    WifiManager wifiManager;
    HashMap<String, Integer> hmFingerprint = new HashMap<String, Integer>();
    List scanResultsList;
    int scanCounter;
    int mInterval = 1000;
    Handler mHandler = new Handler();
    OfflineScan offlineScan = new OfflineScan();
    Runnable statusChecker;
    Datasource datasource;
    ArrayList fpList;
    String direction;
    int x;
    int y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanbox);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        x = bundle.getInt("x");
        y = bundle.getInt("y");

        direction = getDirection(y);
        TextView tvDirection = (TextView) findViewById(R.id.tvDirection);
        tvDirection.setText(tvDirection.getText() + " " + direction);

        Button bDetails = (Button) findViewById(R.id.btn_Details);
        // Display Details button
        if (!isNavCellEmpty()) {
            bDetails.setVisibility(View.VISIBLE);
        } else {
            bDetails.setVisibility(View.INVISIBLE);
        }

        TextView getX = (TextView)findViewById(R.id.getX);
        TextView getY = (TextView)findViewById(R.id.getY);

        getX.setText(String.valueOf(x));
        getY.setText(String.valueOf(y));

        // Start the wifi scan
        mHandler = new Handler();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        statusChecker = new Runnable() {
            @Override
            public void run() {

                offlineScan.startScan(wifiManager);
                scanCounter++;

                TextView tvScansCompleted = (TextView) findViewById(R.id.tvScansCompleted);
                tvScansCompleted.setText("Scans Completed: " + scanCounter);

                mHandler.postDelayed(this, mInterval);

                if (scanCounter == 10) {
                    Button bStopScan = (Button)findViewById(R.id.btn_stop);
                    bStopScan.performClick();
                }
            }
        };
    }

    private boolean isNavCellEmpty() {
        datasource = new Datasource(this, "navigation_table");
        datasource.open();
        Cell cell = datasource.getCell(x, y);
        return cell == null;
    }

    public void startScan(View view) {
        scanCounter = 0;
        startUpdates();
    }

    public void viewScans(View view) {
        ArrayList<String> alScans = new ArrayList<String>();
        String str = "";
        for (Map.Entry<String, Integer> entry : hmFingerprint.entrySet()) {
            str = entry.getKey() + " | " + entry.getValue();
            alScans.add(str);
        }
        Intent intent = new Intent(ScanActivity.this, ViewActivity.class);
        intent.putExtra("wifiScanList", alScans);

        startActivity(intent);
    }

    public void stopScan(View view) {
        stopUpdates();

        // Compute Mean RSS for each entry in hashmap
        fpList = offlineScan.computeMeanRSS();
    }

    public void saveData(View view) {
        // Save fingerprints to Database
        datasource = new Datasource(this, "fingerprint_table");
        datasource.open();

        int fpCounter = 0;

        Datasource navDS = new Datasource(this, "navigation_table");
        navDS.open();

        // Get max NavCellID from navigation_table
        int nextNavCellId = 0;
        nextNavCellId = datasource.getMaxNavCellId();
        nextNavCellId += 1;

        HashMap<String, Fingerprint> hmFingerprint = datasource.getFingerprint(x, y);
        for (Object fingerprint : fpList) {
            Fingerprint fp = (Fingerprint) fingerprint;
            // if record already exists, take mean rss and update record
            // else, add new record
            // check for duplicates in hashmap
            if (hmFingerprint.containsKey(fp.getBssid()) &&WifiManager.calculateSignalLevel(fp.getRss(),100) >24  && hmFingerprint.size() > 0) {
                // update hashmap
                Fingerprint fingerprint1 = hmFingerprint.get(fp.getBssid());

                // get mean value
                int meanRssFromDB = fingerprint1.getRss();
                int meanRssFromScan = fp.getRss();

                // update mean value
                int finalMeanValue = (meanRssFromDB + meanRssFromScan) / 2;
                fingerprint1.setRss(finalMeanValue);

                // update database with new rss value
                datasource.updateFingerprint(fp);
            } else {

                int fpId = (int) datasource.insertFingerprint(fp);

                // Save a record into navigation table
                NavCell navCell = new NavCell();
                navCell.setXCord(x);
                navCell.setYCord(y);
                navCell.setDirection(direction);
                navCell.setFpId(fpId);
                navCell.setNavCellId(nextNavCellId);

                navDS.insertNavCell(navCell);

                fpCounter++;
            }
        }
        datasource.close();
        navDS.close();

        // Display a notification as to how many fingerprints were stored
        if (fpCounter > 0) {
            Toast.makeText(getApplicationContext(), fpCounter + " fingerprints saved to DB", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No new fingerprints", Toast.LENGTH_LONG).show();
        }

        // Update neighboring park cells
/*
        datasource = new Datasource(this, "parking_table");
        datasource.open();
        datasource.updateParkCell(x, y - 1, nextNavCellId);
        datasource.updateParkCell(x, y + 1, nextNavCellId);
        datasource.close();
*/

        finish();
    }

    public String getDirection(int y) {
        switch (y % 12) {
            case 1:
            case 4:
                return "South";
            case 7:
            case 10:
                return "North";
            default:
                return null;
        }
    }

    public void discardData(View view) {
        // Discard all fingerprints recorded in that scan
        try {
            fpList.clear();
        } catch (Exception ignored) {

        }
        Toast.makeText(getApplicationContext(), "All fingerprints discarded", Toast.LENGTH_LONG).show();
        finish();
    }

    public void singleScan(View view){

        //This method will be called only once.
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        scanResultsList = wifiManager.getScanResults();
        ArrayList<String> wifiList = new ArrayList<String>();
        String str ="";
        String ssid;
        for(Object aScanResultList: scanResultsList){
            ScanResult scanResult = (ScanResult)aScanResultList;
             if (WifiManager.calculateSignalLevel(scanResult.level ,100) > 24) {
                ssid = scanResult.BSSID + " | " + scanResult.SSID + " | " + scanResult.level;
                wifiList.add(ssid);
            }

        }
       Toast.makeText(getApplicationContext(),str , Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ScanActivity.this, ViewActivity.class);

        intent.putExtra("wifiScanList" , wifiList);
        startActivity(intent);
    }

    public void clearDB(View view) {
//        datasource.deleteFingerprint();
    }

    public synchronized void startUpdates() {
        statusChecker.run();
    }

    public synchronized void stopUpdates() {
        mHandler.removeCallbacks(statusChecker);
    }
}
