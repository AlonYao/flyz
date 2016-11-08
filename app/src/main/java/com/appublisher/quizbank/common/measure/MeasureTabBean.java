package com.appublisher.quizbank.common.measure;

/**
 * 做题模块：Tab
 */

public class MeasureTabBean {

    private int position;
    private String name;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
