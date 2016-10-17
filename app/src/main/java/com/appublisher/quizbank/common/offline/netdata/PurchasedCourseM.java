package com.appublisher.quizbank.common.offline.netdata;

import java.util.ArrayList;

/**
 * 已购课程
 */
public class PurchasedCourseM {

    int id;
    String name;
    ArrayList<PurchasedClassM> classes;
    String lector;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PurchasedClassM> getClasses() {
        return classes;
    }

    public String getLector() {
        return lector;
    }
}