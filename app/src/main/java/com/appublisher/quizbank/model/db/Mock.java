package com.appublisher.quizbank.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 模考信息
 */
public class Mock extends Model{
    @Column(name = "paper_id")
    public int paper_id;

    @Column(name = "date")
    public int date;
}

