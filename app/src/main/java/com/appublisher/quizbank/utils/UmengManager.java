package com.appublisher.quizbank.utils;

import android.content.Context;

import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟管理
 */
public class UmengManager {

    /**
     * 发送计数事件
     * @param context 引用
     * @param event_id 事件id
     * @param param 事件Action
     * @param desc 事件描述
     */
    public static void sendCountEvent(Context context,
                                      String event_id,
                                      String param,
                                      String desc) {
        HashMap<String, String> map = new HashMap<>();
        map.put(param, desc);
        MobclickAgent.onEvent(context, event_id, map);
    }

    /**
     * 发送计算事件
     * @param context 引用
     * @param event_id 事件id
     * @param map 事件Map
     * @param duration 时长（单位：秒）
     */
    public static void sendComputeEvent(Context context,
                                        String event_id,
                                        HashMap<String, String> map,
                                        int duration) {
        MobclickAgent.onEventValue(context, event_id, map, duration);
    }

    /**
     * 练习部分统计结构
     * @param entry param-起
     * @param done param-终
     * @return map
     */
    public static HashMap<String, String> umengMeasureMap(String entry, String done) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Entry", entry);
        map.put("Done", done);
        return map;
    }

    /**
     * 练习统计发送到Umeng(练习页面)
     * @param activity MeasureActivity
     * @param done 离开练习的状态
     */
    public static void sendToUmeng(MeasureActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(练习解析页面)
     * @param activity MeasureActivity
     * @param done 离开练习的状态
     */
    public static void sendToUmeng(MeasureAnalysisActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mAnalysisType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(答题卡页面)
     * @param activity AnswerSheetActivity
     * @param done 离开练习的状态
     */
    public static void sendToUmeng(AnswerSheetActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(练习报告页面)
     * @param activity PracticeReportActivity
     * @param done 离开练习的状态
     */
    public static void sendToUmeng(PracticeReportActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }
}
