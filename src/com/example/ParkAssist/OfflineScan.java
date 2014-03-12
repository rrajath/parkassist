package com.example.ParkAssist;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rrajath on 3/11/14.
 */
public class OfflineScan {
    HashMap<String, Integer> hmFingerprint = new HashMap<String, Integer>();
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
    }

    public ArrayList computeMeanRSS() {

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
