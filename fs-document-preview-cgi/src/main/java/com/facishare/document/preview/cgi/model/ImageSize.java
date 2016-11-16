package com.facishare.document.preview.cgi.model;

/**
 * Created by liuq on 2016/11/16.
 */
public class ImageSize {

    private int width;
    private int height;

    public ImageSize(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
