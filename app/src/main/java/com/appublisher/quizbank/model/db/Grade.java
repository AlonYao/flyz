package com.appublisher.quizbank.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 用户评价表
 */
public class Grade extends Model{

    // 版本号
    @Column(name = "app_version")
    public String app_version;

    // 时间戳
    @Column(name = "timestamp")
    public long timestamp;

    // 是否评价过 0:false 1:true
    @Column(name = "is_grade")
    public int is_grade;

    // 用户点击评价时的时间戳
    @Column(name = "grade_timestamp")
    public long grade_timestamp;

    // 记录用户当天第一次离开练习报告页或者能力评估页的日期
    @Column(name = "first_leave")
    public String first_leave;
}
