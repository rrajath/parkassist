package com.example.WiFiInfo;

/**
 * Created by rrajath on 2/21/14.
 */
public class Fingerprint {
    private int id;
    private String bssid;
    private String ssid;
    private int rss;
    private String refPoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public void setRefPoint(String ref){this.refPoint=ref;}
    public String getRefPoint(){return refPoint;}
}
