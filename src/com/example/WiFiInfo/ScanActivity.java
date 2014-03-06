package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
    Runnable statusChecker;
    Datasource datasource;

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

        datasource = new Datasource(this, "fingerprint_table");
        datasource.open();

        //  datasource.refreshDB();
        // Start the wifi scan
        mHandler = new Handler();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        statusChecker = new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();

                // List of access points scanned
                scanResultsList = wifiManager.getScanResults();

                // Initialize hmFingerprint everytime the Scan button is pressed
                hmFingerprint = new HashMap<String, Integer>();

                // Iterate through the scan results and add new RSS values to hashmap
                for (Object aScanResultsList : scanResultsList) {
                    ScanResult scanResult = (ScanResult) aScanResultsList;

                    // Add <BSSID|SSID,RSS> to hashmap
                    String hFPKey = scanResult.BSSID + "|" + scanResult.SSID;
                    int sumRss;
                    if (hmFingerprint.get(hFPKey) != null) {
                        sumRss = hmFingerprint.get(hFPKey) + scanResult.level;
                        hmFingerprint.put(hFPKey, sumRss);
                    } else {
                        hmFingerprint.put(hFPKey, scanResult.level);
                    }
                }
                scanCounter++;

                mHandler.postDelayed(this, mInterval);
            }
        };
    }

    public void startScan(View view) {
        scanCounter = 0;
        startUpdates();
    }

    public void stopScan(View view) {
        stopUpdates();

        // Compute Mean RSS for each entry in hashmap
        computeMeanRSS();
    }

    public void computeMeanRSS() {

        /*
         * Iterate through hashmap, extract BSSID, SSID and Mean RSS of each access point
         * and store it in the fingerprint_table
         */
        for (Map.Entry<String, Integer> entry : hmFingerprint.entrySet()) {
            Fingerprint fingerprint = new Fingerprint();

            // Split the pipe separated key into BSSID and SSID and compute Mean RSS value
            String key = entry.getKey();
            String bssid = key.substring(0, key.indexOf("|"));
            String ssid = key.substring(key.indexOf("|") + 1, key.length());
            int meanRSS = entry.getValue() / scanCounter;

            // Store it in a Fingerprint object to insert it into database
            fingerprint.setSsid(ssid);
            fingerprint.setBssid(bssid);
            fingerprint.setRss(meanRSS);

            datasource.insertFingerprint(fingerprint);
        }
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
