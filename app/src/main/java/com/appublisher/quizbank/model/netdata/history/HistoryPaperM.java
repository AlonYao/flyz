package com.appublisher.quizbank.model.netdata.history;

/**
 * 学习历史数据模型
 */
public class HistoryPaperM {

    private int paper_id;
    private String paper_type;
    private String name;
    private float accuracy;
    private String status;
    private String action_time;
    private int hierarchy_level;
    private int hierarchy_id;

    public int getPaper_id() {
        return paper_id;
    }

    public void setPaper_id(int paper_id) {
        this.paper_id = paper_id;
    }

    public String getPaper_type() {
        return paper_type;
    }

    public void setPaper_type(String paper_type) {
        this.paper_type = paper_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction_time() {
        return action_time;
    }


    public void setAction_time(String action_time) {
        this.action_time = action_time;
    }

    public int getHierarchy_level() {
        return hierarchy_level;
    }

    public void setHierarchy_level(int hierarchy_level) {
        this.hierarchy_level = hierarchy_level;
    }

    public int getHierarchy_id() {
        return hierarchy_id;
    }

    public void setHierarchy_id(int hierarchy_id) {
        this.hierarchy_id = hierarchy_id;
    }

}
