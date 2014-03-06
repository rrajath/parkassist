package com.example.WiFiInfo;

/**
 * Created by rrajath on 2/21/14.
 */
public class Fingerprint {
    private int fpId;
    private String bssid;
    private String ssid;
    private int rss;

    public int getFpId() { return fpId; }

    public void setFpId(int fpId) { this.fpId = fpId; }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getRss() {
        return rss;
    }

    public void setRss(int rss) {
        this.rss = rss;
    }

}
