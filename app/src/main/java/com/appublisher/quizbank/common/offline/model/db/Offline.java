package com.appublisher.quizbank.common.offline.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 离线视频表
 */
public class Offline extends Model {

    @Column(name = "purchased_data")
    public String purchased_data;

    @Column(name = "room_id")
    public String room_id;

    @Column(name = "is_success")
    public int is_success;

    @Column(name = "course_id")
    public int course_id;
}
