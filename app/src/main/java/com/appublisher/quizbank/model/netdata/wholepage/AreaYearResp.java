package com.appublisher.quizbank.model.netdata.wholepage;

import java.util.ArrayList;

/**
 * 地区和年份回调
 */
public class AreaYearResp {

    int response_code;
    ArrayList<AreaM> area;
    ArrayList<Integer> year;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public ArrayList<AreaM> getArea() {
        return area;
    }

    public void setArea(ArrayList<AreaM> area) {
        this.area = area;
    }

    public ArrayList<Integer> getYear() {
        return year;
    }

    public void setYear(ArrayList<Integer> year) {
        this.year = year;
    }
}
