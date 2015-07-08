package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;
import java.util.List;

/**
 * 课程列表 课程item 数据模型
 */
public class CourseM {

    private int id;
    private ArrayList<String> lectors;
    private boolean is_sticked;
    private String status;
    private String name;
    private String detail_page;
    private String type;
    private String introduction;
    private boolean is_purchased;

    public int getId() {
        return id;
    }

    public ArrayList<String> getLectors() {
        return lectors;
    }

    public boolean isIs_sticked() {
        return is_sticked;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDetail_page() {
        return detail_page;
    }

    public String getType() {
        return type;
    }

    public String getIntroduction() {
        return introduction;
    }

    public boolean isIs_purchased() {
        return is_purchased;
    }
}
