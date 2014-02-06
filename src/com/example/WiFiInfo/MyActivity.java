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

import java.util.List;

public class MyActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */

    Sensor acceleration;
    SensorManager sensorManager;
    TextView textView;
    TextView accelerometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
//        String ssid = "";
//                "SSID:" + info.getSSID() + "\n" +
//                "RSSI:" + info.getRssi() + "\n" +
//                "Configured Networks:" + wifiManager.getConfiguredNetworks() + "\n" +
//                "Scan Results:" + wifiManager.getScanResults();
//                "Calculated Signal Level:" + WifiManager.calculateSignalLevel(info.getRssi(), 10);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleration = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_NORMAL);
        accelerometer = (TextView)findViewById(R.id.accelerometer);
    }

    public void scan(View view) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        String ssid = new String();

        List scanResultsList = wifiManager.getScanResults();
        for (int i=0; i<scanResultsList.size(); i++) {
            ScanResult scanResult = (ScanResult)scanResultsList.get(i);
            ssid += scanResult.SSID + " | " + scanResult.BSSID + ":" +
                    WifiManager.calculateSignalLevel(scanResult.level, 100) + "\n";
        }
        textView = (TextView)findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(ssid);

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
