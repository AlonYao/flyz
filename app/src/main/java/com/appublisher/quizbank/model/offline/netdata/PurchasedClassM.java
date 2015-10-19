package com.appublisher.quizbank.model.offline.netdata;

import java.io.Serializable;

/**
 * 已购课堂
 */
public class PurchasedClassM implements Serializable{

    int id;
    String name;
    String room_id;
    String start_time;
    String end_time;
    String lector;
    int status;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getLector() {
        return lector;
    }

    public int getStatus() {
        return status;
    }
}
