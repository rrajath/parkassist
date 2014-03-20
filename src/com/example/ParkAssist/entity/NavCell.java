package com.example.ParkAssist.entity;

/**
 * Created by rrajath on 3/6/14.
 */
public class NavCell {

    private int navCellId;
    private int fpId;
    private String direction;
    private static  int xCord;
    private static int yCord;

    public void setNavCellId(int id) {
        this.navCellId = id;
    }

    public int getNavCellId() {
        return navCellId;
    }

    public int getFpId() { return fpId; }

    public void setFpId(int fpId) { this.fpId = fpId; }

    public void setDirection(String dir) {
        this.direction = dir;
    }

    public String getDirection() {
        return direction;
    }

    public void setXCord(int x) {
       xCord = x;
    }

    public int getXCord() {
        return xCord;
    }

    public void setYCord(int y) {
        yCord = y;
    }

    public int getYCord() {
        return yCord;
    }

}
