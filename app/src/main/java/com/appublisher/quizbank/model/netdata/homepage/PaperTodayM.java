package com.appublisher.quizbank.model.netdata.homepage;

/**
 * 今日模考数据模型
 */
public class PaperTodayM {

    int id;
    boolean done;
    int persons_num;
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPersons_num() {
        return persons_num;
    }

    public void setPersons_num(int persons_num) {
        this.persons_num = persons_num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
