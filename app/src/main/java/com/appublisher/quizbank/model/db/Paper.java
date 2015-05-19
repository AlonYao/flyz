package com.appublisher.quizbank.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 试卷信息
 */
public class Paper extends Model {

    @Column(name = "paper_id")
    public int paper_id;

    @Column(name = "last_position")
    public int last_position;
}
