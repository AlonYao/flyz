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

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setLector(String lector) {
        this.lector = lector;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
