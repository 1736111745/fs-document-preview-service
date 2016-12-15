package com.facishare.document.preview.common.utils;

/**
 * Created by liuq on 16/9/9.
 */
public enum DocType {
    Word("word", 0), Excel("excel", 1), PPT("ppt", 2), PDF("pdf", 3), Other("other", 4);
    // 成员变量
    private String name;
    private int index;

    DocType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // 普通方法
    public static String getName(int index) {
        for (DocType c : DocType.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    }

