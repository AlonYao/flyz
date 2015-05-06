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
}
