package com.myapplication3.parichay;

public class news_post {

    public String image,title,date,desc;

    public news_post() {}

    public news_post(String image, String title, String date, String desc) {
        this.image = image;
        this.title = title;
        this.date = date;
        this.desc = desc;
    }

    public String getImgUrl() {
        return image;
    }

    public void setImgUrl(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
