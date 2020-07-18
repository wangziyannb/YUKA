package com.wzy.yuka.ui.view;

/**
 * Created by Ziyan on 2020/5/17.
 */
public class Screenshot {
    private String picSrc;
    private String date;

    public Screenshot(String picSrc, String date) {
        this.picSrc = picSrc;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getPicSrc() {
        return picSrc;
    }
}
