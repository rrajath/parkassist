package com.example.ParkAssist.util;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import com.example.ParkAssist.entity.Fingerprint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rrajath on 3/11/14.
 */
public class OfflineScan {
    HashMap<String, Integer> hmFingerprint = new HashMap<String, Integer>();
    HashMap<String, Integer> hmScanCounter = new HashMap<String, Integer>();
    List scanResultsList;
    int scanCounter;

    public void startScan(WifiManager wifiManager) {

        wifiManager.startScan();

        // List of access points scanned
        scanResultsList = wifiManager.getScanResults();

        // Initialize hmFingerprint everytime the Scan button is pressed
//        hmFingerprint = new HashMap<String, Integer>();

        // Iterate through the scan results and add new RSS values to hashmap
        for (Object aScanResultsList : scanResultsList) {
            ScanResult scanResult = (ScanResult) aScanResultsList;

            // Add <BSSID|SSID,RSS> to hashmap
            if(WifiManager.calculateSignalLevel(scanResult.level,100)>24){
            String hFPKey = scanResult.BSSID + "|" + scanResult.SSID;
            int sumRss;
            if (hmFingerprint.get(hFPKey) != null) {
                sumRss = hmFingerprint.get(hFPKey) + WifiManager.calculateSignalLevel(scanResult.level ,100);
                hmFingerprint.put(hFPKey, sumRss);
            } else {
                hmFingerprint.put(hFPKey, WifiManager.calculateSignalLevel(scanResult.level ,100));
            }

            // Check if the value exists in hmScanCounter HashMap
            int counter;
            if (hmScanCounter.get(scanResult.BSSID) != null) {
                counter = hmScanCounter.get(scanResult.BSSID);
                counter += 1;
            } else {
                counter = 1;
            }
            hmScanCounter.put(scanResult.BSSID, counter);

        }
    }}

    public ArrayList  computeMeanRSS() {

        ArrayList<Fingerprint> fpList = new ArrayList<Fingerprint>();

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

            // Get scanCounter value from hashmap
            scanCounter = hmScanCounter.get(bssid);
            int meanRSS = entry.getValue() / scanCounter;
            hmFingerprint.put(key, meanRSS);

            // Store it in a Fingerprint object to insert it into database
            fingerprint.setSsid(ssid);
            fingerprint.setBssid(bssid);
            fingerprint.setRss(meanRSS);

            fpList.add(fingerprint);
        }

        return fpList;
    }

}
