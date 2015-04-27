package com.appublisher.quizbank.model.netdata.historymokao;

/**
 * 历史模考数据模型
 */
public class HistoryMokaoM {

    int id;
    String name;
    String date;
    int persons_num;
    String status;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPersons_num() {
        return persons_num;
    }

    public void setPersons_num(int persons_num) {
        this.persons_num = persons_num;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}