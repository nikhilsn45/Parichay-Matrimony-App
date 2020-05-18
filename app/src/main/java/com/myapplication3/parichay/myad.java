package com.myapplication3.parichay;

public class myad {

    public String adimg;
    public String adtitle;
    public String c_date;
    public String expiry;
    public String duration;
    public String status;

    public myad() {}

    public myad(String adimg, String adtitle, String c_date, String expiry,String duration,String status) {
        this.adimg = adimg;
        this.adtitle = adtitle;
        this.c_date = c_date;
        this.expiry = expiry;
        this.duration = duration;
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAdimg() {
        return adimg;
    }

    public void setAdimg(String adimg) {
        this.adimg = adimg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdtitle() {
        return adtitle;
    }

    public void setAdtitle(String adtitle) {
        this.adtitle = adtitle;
    }

    public String getC_date() {
        return c_date;
    }

    public void setC_date(String c_date) {
        this.c_date = c_date;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
