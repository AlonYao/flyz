package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;

/**
 * 课程列表 课程item 数据模型
 */
public class CourseM {

    private int periods;
    private String status;
    private int persons_num;
    private String detail_page;
    private String type;
    private int id;
    private ArrayList<String> lectors;
    private boolean is_sticked;
    private int price;
    private String end_time;
    private String name;
    private String start_time;
    private String introduction;
    private boolean is_purchased;

    public int getPeriods() {
        return periods;
    }

    public String getStatus() {
        return status;
    }

    public int getPersons_num() {
        return persons_num;
    }

    public String getDetail_page() {
        return detail_page;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getLectors() {
        return lectors;
    }

    public boolean is_sticked() {
        return is_sticked;
    }

    public int getPrice() {
        return price;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getName() {
        return name;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getIntroduction() {
        return introduction;
    }

    public boolean is_purchased() {
        return is_purchased;
    }
}
