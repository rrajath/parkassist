package com.example.WiFiInfo;

/**
 * Created by rrajath on 2/21/14.
 */
public class Fingerprint {
    private int id;
    private String bssid;
    private String ssid;
    private int rss;
    private int navid;
    private  int parkid;
    private  int xCord;
    private int yCord;
    private  String direction;

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

    public void setNavid(int id){this.navid = id; }

    public  int getNavid(){  return  navid; }

    public void setParkid(int id ){this.parkid = id; }

    public int getParkid(){ return parkid;  }

    public void setXCord(int x){this.xCord = x; }

    public int getXCord(){ return xCord;}

    public void setYCord(int y){this.yCord = y;}

    public int  getYCord(){ return yCord;}

    public void setDirection(String dir){this.direction = dir; }

    public String getDirection(){ return  direction;}

}
