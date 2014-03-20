package com.example.ParkAssist.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ParkAssist.database.Datasource;
import com.example.ParkAssist.entity.Fingerprint;
import com.example.ParkAssist.entity.NavCell;
import com.example.ParkAssist.util.OfflineScan;
import com.example.ParkAssist.R;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanbox);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.direction, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        NavCell nav = new NavCell();
        int s = nav.getXCord();
        TextView getX = (TextView)findViewById(R.id.getX);
        TextView getY = (TextView)findViewById(R.id.getY);

        getX.setText(""+ nav.getXCord());
        getY.setText("" + nav.getYCord());

        datasource = new Datasource(this, "fingerprint_table");
        datasource.open();

        //  datasource.refreshDB();
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
            }
        };
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
        for (Object fingerprint : fpList) {
            datasource.insertFingerprint((Fingerprint) fingerprint);
        }
        Toast.makeText(getApplicationContext(), fpList.size() + " fingerprints saved to DB", Toast.LENGTH_LONG).show();
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
        String ssid;
        for(Object aScanResultList: scanResultsList){
            ScanResult scanResult = (ScanResult)aScanResultList;
            ssid = scanResult.BSSID + " | " + scanResult.SSID + " | " + scanResult.level;
            wifiList.add(ssid);
        }

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