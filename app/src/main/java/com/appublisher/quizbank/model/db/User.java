package com.appublisher.quizbank.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 用户表
 */
public class User extends Model{

    @Column(name = "user")
    public String user;

    @Column(name = "exam")
    public String exam;
}
