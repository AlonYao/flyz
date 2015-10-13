package com.appublisher.quizbank.model.offline.netdata;

import java.util.ArrayList;

/**
 * 已购课程
 */
public class PurchasedCourseM {

    int id;
    String name;
    ArrayList<PurchasedClassM> classes;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PurchasedClassM> getClasses() {
        return classes;
    }
}
