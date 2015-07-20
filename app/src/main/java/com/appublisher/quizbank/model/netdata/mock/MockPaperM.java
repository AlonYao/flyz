package com.appublisher.quizbank.model.netdata.mock;

/**
 * 模考&估分列表单项的数据模型
 */
public class MockPaperM {

    int id;
    int exercise_id;
    String name;
    String status;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getExercise_id() {
        return exercise_id;
    }
}
