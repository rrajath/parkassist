package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
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
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.direction, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        datasource = new FingerprintDS(this , "fingerprint_table");
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
        ssCounter = 0;
        startUpdates();
    }

    public void stopScan(View view) {
        stopUpdates();
    }

    public void   mean(View view) {
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
       // datasource.deleteFingerprint();
        tvScans = (TextView) findViewById(id.tvScan);
        tvScans.setText("");
    }



    public void singleScan(View view){


        //This method will be  called only once.
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        scanResultsList = wifiManager.getScanResults();
        ArrayList<String> wifiList = new ArrayList<String>();
        ssid ="";
        String scan = "";
        for(Object ascanresultList: scanResultsList){
            ScanResult scanResult = (ScanResult)ascanresultList;
            ssid = scanResult.BSSID + scanResult.SSID + wifiManager.calculateSignalLevel(scanResult.level ,100);
            wifiList.add(ssid);


        }


        Intent intent = new Intent(ScanActivity.this, ViewActivity.class);

        intent.putExtra("wifiScanList" , wifiList);
        startActivity(intent);
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
