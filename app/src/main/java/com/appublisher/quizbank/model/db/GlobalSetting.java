package com.appublisher.quizbank.model.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * 用于保存全局配置信息的数据库表
 */
public class GlobalSetting extends Model{

    // JSONObject 内容
    @Column(name = "content")
    public String content;

    // 最近一条通知
    @Column(name = "latest_notify")
    public int latest_notify;

    // 使用次数
    @Column(name = "use_count")
    public int use_count;

    // 是否评价过
    @Column(name = "is_grade")
    public boolean is_grade;
}
