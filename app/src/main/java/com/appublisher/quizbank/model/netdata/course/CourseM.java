package com.appublisher.quizbank.model.netdata.course;

import java.util.ArrayList;
import java.util.List;

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
    private String price;
    private String end_time;
    private String name;
    private String start_time;
    private String introduction;
    private boolean is_purchased;
    /**
     * id : 449
     * name : 国考智学数量3
     * start_time : 2016-08-30 22:00:00
     * end_time : 2016-08-30 23:30:00
     * lector : 齐麟
     */

    private List<TodayClassesBean> today_classes;

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

    public String getPrice() {
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

    public List<TodayClassesBean> getToday_classes() {
        return today_classes;
    }

    public void setToday_classes(List<TodayClassesBean> today_classes) {
        this.today_classes = today_classes;
    }

    public static class TodayClassesBean {
        private int id;
        private String name;
        private String start_time;
        private String end_time;
        private String lector;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getLector() {
            return lector;
        }

        public void setLector(String lector) {
            this.lector = lector;
        }
    }
}
