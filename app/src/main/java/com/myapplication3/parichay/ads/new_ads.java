package com.myapplication3.parichay.ads;

public class new_ads {

    public String adtitle ,adimg,expiry;

    public new_ads() {}

    public new_ads(String adtitle, String adimg, String expiry) {
        this.adtitle = adtitle;
        this.adimg = adimg;
        this.expiry = expiry;
    }

    public String getAdtitle() {
        return adtitle;
    }

    public void setAdtitle(String adtitle) {
        this.adtitle = adtitle;
    }

    public String getAdimg() {
        return adimg;
    }

    public void setAdimg(String adimg) {
        this.adimg = adimg;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }
}
