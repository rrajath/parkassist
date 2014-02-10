package com.example.WiFiInfo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */

    Sensor acceleration;
    SensorManager sensorManager;
    TextView tvScans;
    TextView tvUpdated;
    TextView accelerometer;
    WifiManager wifiManager;
    WifiInfo info;
    String ssid;
    HashMap<String, Integer> hRss = new HashMap<String, Integer>();
    HashMap<Integer, HashMap> hRefPoints = new HashMap<Integer, HashMap>();
    List scanResultsList;
    int ssCounter;
    int rpCounter;
    int sum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        info = wifiManager.getConnectionInfo();
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
//        accelerometer = (TextView)findViewById(R.id.accelerometer);
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
        for (int i=0; i<scanResultsList.size(); i++) {
            ScanResult scanResult = (ScanResult)scanResultsList.get(i);
            ssid = scanResult.SSID;
            if (hRss.get(ssid) != null) {
                sum = (int)hRss.get(ssid);
                sum += WifiManager.calculateSignalLevel(scanResult.level, 100);
            }
            else {
                sum = WifiManager.calculateSignalLevel(scanResult.level, 100);
            }
            hRss.put(ssid, sum);
            scans += ssid + " : " + sum + "\n";
        }
        ssCounter++;

        tvScans = (TextView)findViewById(R.id.tvScan);
        tvScans.setMovementMethod(new ScrollingMovementMethod());
        tvScans.setText(scans);

    }

    // Avg of values will be taken and HashMap will be replaced with the final value.
    public void update(View view) {
        int ssSum = 0;
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

        tvUpdated = (TextView)findViewById(R.id.tvUpdated);
        tvUpdated.setMovementMethod(new ScrollingMovementMethod());
        tvUpdated.setText(output);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accelerometer.setText("X: " + event.values[0] +
            "\nY: " + event.values[1] +
            "\nZ: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
