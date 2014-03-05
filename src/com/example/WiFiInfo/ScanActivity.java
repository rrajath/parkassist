package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
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

public class ScanActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    TextView tvScans;
    TextView tvMean;
    TextView tvScansCompleted;
    WifiManager wifiManager;
    String ssid;
    String refPoint;
    HashMap<String, Integer> hRss = new HashMap<String, Integer>();
    Fingerprint fingerprint;
    List scanResultsList;
    int ssCounter;
    int sum;
    int mInterval = 1000;
    Handler mHandler = new Handler();
    Runnable statusChecker;
    FingerprintDS datasource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.scanbox);

        refPoint = getIntent().getExtras().getString("thepoint");
        datasource = new FingerprintDS(this);
        datasource.open();

        datasource.refreshDB();
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
                    fingerprint.setRefPoint(refPoint);

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
        ssCounter = 0;
        startUpdates();
    }

    public void stopScan(View view) {
        stopUpdates();
    }

    public void mean(View view) {
        HashMap<String, Double> hmMean;
        hmMean = datasource.getMeanValue(ssCounter);
        StringBuilder meanValues = new StringBuilder();
        for (Map.Entry<String, Double> entry : hmMean.entrySet()) {
//            meanValues += entry.getKey() + " | " + entry.getValue() + "\n";
            meanValues.append(entry.getKey()).append(" | ").append(entry.getValue()).append("\n");
        }
        tvMean = (TextView) findViewById(id.tvMean);
        tvScans.setMovementMethod(new ScrollingMovementMethod());
        tvMean.setText(meanValues);
    }

    public void clearDB(View view) {
        datasource.deleteFingerprint();
        tvScans = (TextView) findViewById(id.tvScan);
        tvScans.setText("");
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
}
