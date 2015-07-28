package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.HashMap;

/**
 * 友盟管理
 */
public class UmengManager {

    private static final UMSocialService mController =
            UMServiceFactory.getUMSocialService("com.umeng.share");

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

    /**
     * 打开分享列表
     * @param activity Activity
     * @param content 分享文字
     * @param bitmap bitmap
     */
    public static void openShare(Activity activity, String content, Bitmap bitmap) {
        mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);

        UMImage umImage = new UMImage(activity, bitmap);

        // 微信分享
        UMWXHandler wxHandler = new UMWXHandler(
                activity,
                activity.getString(R.string.weixin_appid),
                activity.getString(R.string.weixin_secret));
        wxHandler.addToSocialSDK();
        WeiXinShareContent weixin = new WeiXinShareContent();
        weixin.setShareContent(content);
        mController.setShareMedia(weixin);

        // 微信朋友圈分享
        UMWXHandler wxCircleHandler = new UMWXHandler(
                activity,
                activity.getString(R.string.weixin_appid),
                activity.getString(R.string.weixin_secret));
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        CircleShareContent weixinCircle = new CircleShareContent();
        weixinCircle.setShareContent(content);
        mController.setShareMedia(weixinCircle);

        // QQ分享
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(
                activity,
                activity.getString(R.string.qq_appid),
                activity.getString(R.string.qq_appkey));
        qqSsoHandler.addToSocialSDK();
        QQShareContent qq = new QQShareContent();
        qq.setShareContent(content);
        qq.setShareImage(umImage);
        mController.setShareMedia(qq);

        // Qzone分享
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
                activity,
                activity.getString(R.string.qq_appid),
                activity.getString(R.string.qq_appkey));
        qZoneSsoHandler.addToSocialSDK();
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(content);
        mController.setShareMedia(qzone);
        
        mController.openShare(activity, false);
    }
}
