package com.facishare.document.preview.cgi.convertor;


import application.dcs.Convert;

/**
 * Created by liuq on 16/8/8.
 */
public class ConvertorObject {

    private int id;
    private Convert convertor;
    private boolean available;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Convert getConvertor() {
        return convertor;
    }

    public void setConvertor(Convert convertor) {
        this.convertor = convertor;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
