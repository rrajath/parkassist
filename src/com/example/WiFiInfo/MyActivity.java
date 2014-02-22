package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.WiFiInfo.R.id;
import static com.example.WiFiInfo.R.layout;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    TextView tvScans;
    TextView tvUpdated;
    TextView tvScansCompleted;
    WifiManager wifiManager;
    WifiInfo info;
    String ssid;
    HashMap<String, Integer> hRss = new HashMap<String, Integer>();
    HashMap<Integer, HashMap> hRefPoints = new HashMap<Integer, HashMap>();
    Fingerprint fingerprint;
    List scanResultsList;
    int ssCounter;
    int rpCounter;
    int sum;
    int mInterval = 1000;
    Handler mHandler = new Handler();
    int i;
    long mStartTime;
    Runnable statusChecker;
    FingerprintDS datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.main);

        datasource = new FingerprintDS(this);
        datasource.open();

        // Start the wifi scan
        mHandler = new Handler();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        statusChecker = new Runnable() {
            @Override
            public void run() {
                wifiManager.startScan();

                // List of access points scanned
                scanResultsList = wifiManager.getScanResults();

                // Capture SSID
                ssid = "";
                String scans = "";

                // Iterate through the scan results and add new RSS values to hashmap
                for (Object aScanResultsList : scanResultsList) {
                    ScanResult scanResult = (ScanResult) aScanResultsList;
                    fingerprint = new Fingerprint();

                    fingerprint.setSsid(scanResult.SSID);
                    fingerprint.setBssid(scanResult.BSSID);
                    fingerprint.setRss(scanResult.level);

                    datasource.insertFingerprint(fingerprint);
                }
                ssCounter++;

                tvScansCompleted = (TextView)findViewById(id.tvScansCompleted);
                tvScansCompleted.setText(String.valueOf(ssCounter));
                tvScans = (TextView)findViewById(id.tvScan);
                tvScans.setMovementMethod(new ScrollingMovementMethod());
                mHandler.postDelayed(this, mInterval);
            }
        };
    }

    public void startScan(View view) {
        startUpdates();
    }

    public void stopScan(View view) {
        stopUpdates();
        List<Fingerprint> fingerprintList = datasource.getAllFingerprints();
        String str = "";
        if (fingerprintList.size() == 0) {
            str = "No values found!";
        }
        for (Object aFingerprint : fingerprintList) {
            Fingerprint fp = (Fingerprint)aFingerprint;
        str = str +
                fp.getId() + " | " +
                fp.getBssid() + " | " +
                fp.getSsid() + " | " +
                fp.getRss() + "\n";
//        break;
    }
        tvScans.setText(str);
    }

    public void exportDB(View view) {
        datasource.exportDB();
    }
    public void clearDB(View view) {
        datasource.deleteFingerprint();
    }
    public void scan(View view) {
        // Every time the scan button is pressed, the RSS value for each AP will be captured
        // and added to the corresponding array of size 4

        // Start the wifi scan
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        // Capture SSID
        ssid = "";
        String scans = "";

        // List of access points scanned
        scanResultsList = wifiManager.getScanResults();

        // Iterate through the scan results and add new RSS values to hashmap
        for (Object aScanResultsList : scanResultsList) {
            ScanResult scanResult = (ScanResult) aScanResultsList;
            ssid = scanResult.SSID;
            if (hRss.get(ssid) != null) {
                sum = hRss.get(ssid);
                sum += WifiManager.calculateSignalLevel(scanResult.level, 100);
            } else {
                sum = WifiManager.calculateSignalLevel(scanResult.level, 100);
            }
            hRss.put(ssid, sum);
            scans += ssid + " : " + sum + "\n";
        }
        ssCounter++;

        tvScans = (TextView)findViewById(id.tvScan);
        tvScans.setMovementMethod(new ScrollingMovementMethod());
        tvScans.setText(scans);

    }

    public synchronized void startUpdates() {
        statusChecker.run();
    }

    public synchronized void stopUpdates() {
        mHandler.removeCallbacks(statusChecker);
    }
    // Avg of values will be taken and HashMap will be replaced with the final value.
    /*public void update() {
        String output = "Final Output:\n";

        // Iterate through the RSS HashMap to take the average value of RSSs from surrounding
        // access points
        for (Map.Entry<String, Integer> entry : hRss.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            value = value/ssCounter;
            hRss.put(key, value);

            output += key + " : " + value + "\n";
        }

        // Increment the ref point counter so that the vector of RSS values are mapped to the
        // corresponding access points
        rpCounter++;

        // Reset the signal strength counter to get new set of RSS values for the next ref point
        ssCounter = 0;

        // Add ref points to a hashmap as (ref_pt_no, hRss) and clear the old hRss hashmap
        hRefPoints.put(rpCounter, hRss);
        hRss.clear();

        tvUpdated = (TextView)findViewById(id.tvUpdated);
        tvUpdated.setMovementMethod(new ScrollingMovementMethod());
        tvUpdated.setText(output);
    }*/
}
