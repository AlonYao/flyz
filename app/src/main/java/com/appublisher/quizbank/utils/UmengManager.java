package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.model.entity.umeng.UMShareContentEntity;
import com.appublisher.quizbank.model.entity.umeng.UMShareUrlEntity;
import com.appublisher.quizbank.model.entity.umeng.UmengShareEntity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.HashMap;
import java.util.Random;

/**
 * 友盟管理
 */
public class UmengManager {

    public static final UMSocialService mController =
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
     * @param umengShareEntity 友盟分享实体类
     */
    public static void openShare(UmengShareEntity umengShareEntity) {
        mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);

        final Activity activity = umengShareEntity.getActivity();

        // 初始化文字
        String content = umengShareEntity.getContent();
        if (content == null || content.length() == 0)
            content = activity.getString(R.string.app_name);

        // 初始化跳转地址
        String url = umengShareEntity.getUrl() == null ? "" : umengShareEntity.getUrl();

        // 初始化图片
        UMImage umImage;
        Bitmap bitmap = umengShareEntity.getBitmap();
        if (bitmap == null) {
            umImage = new UMImage(activity, R.drawable.login_ic_quizbank);
        } else {
            umImage = new UMImage(activity, bitmap);
        }

        // 微信分享
        UMWXHandler wxHandler = new UMWXHandler(
                activity,
                activity.getString(R.string.weixin_appid),
                activity.getString(R.string.weixin_secret));
        wxHandler.addToSocialSDK();
        WeiXinShareContent weixin = new WeiXinShareContent();
        weixin.setShareContent(content);
        weixin.setShareImage(umImage);
        weixin.setTargetUrl(url);
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
        weixinCircle.setShareImage(umImage);
        weixinCircle.setTargetUrl(url);
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
        qq.setTargetUrl(url);
        mController.setShareMedia(qq);

        // Qzone分享
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
                activity,
                activity.getString(R.string.qq_appid),
                activity.getString(R.string.qq_appkey));
        qZoneSsoHandler.addToSocialSDK();
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(content);
        qzone.setShareImage(umImage);
        qzone.setTargetUrl(url);
        mController.setShareMedia(qzone);

        // 新浪微博
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        SinaShareContent sina = new SinaShareContent();
        sina.setShareContent("#天天模考#" + content + url + "(分享自@腰果公务员)#公考要过，就用腰果#");
        sina.setShareImage(umImage);
        mController.setShareMedia(sina);
        
        mController.openShare(activity, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                ToastManager.showToast(activity, "分享中……");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media,
                                   int i,
                                   SocializeEntity socializeEntity) {
                // Empty
            }
        });
    }

    /**
     * 获取分享文字
     * @param umShareContentEntity 分享文字实体类
     * @return 分享文字
     */
    public static String getShareContent(UMShareContentEntity umShareContentEntity) {
        if (umShareContentEntity == null) return "";

        if ("practice_report".equals(umShareContentEntity.getType())) {
            // 练习报告
            if ("mokao".equals(umShareContentEntity.getPaperType())) {
                return "刚刚我在天天模考中打败了全国"
                        + Utils.rateToPercent(umShareContentEntity.getDefeat())
                        + "%的小伙伴，学霸非我莫属！你也想试试？";
            } else if ("evaluate".equals(umShareContentEntity.getPaperType())) {
                return umShareContentEntity.getExamName()
                        + "可以估分了呢，我的预测分是"
                        + umShareContentEntity.getScore()
                        + "分，小伙伴们快来看看~";
            } else {
                return "刷了一套题，正确率竟然达到了"
                        + umShareContentEntity.getAccuracy()
                        + "，你想PK吗？放马过来吧~";
            }

        } else if ("evaluation".equals(umShareContentEntity.getType())) {
            // 能力评估
            float rank = umShareContentEntity.getRank();
            if (rank <= 1 && rank >= 0.75) {
                // 100%-75%
                return "学习Day" + String.valueOf(umShareContentEntity.getLearningDays())
                        + "，我的" + umShareContentEntity.getExamName() + "考试已经刷到了"
                        + String.valueOf(umShareContentEntity.getScore()) + "分，在小伙伴们排名前"
                        + Utils.rateToPercent(rank) + "%，妈妈再也不用担心我的拖延症啦~~";
            } else if (rank < 0.75 && rank >= 0.5) {
                // 75%-50%
                return "学习Day" + String.valueOf(umShareContentEntity.getLearningDays())
                        + "，我的" + umShareContentEntity.getExamName() + "考试已经刷到了"
                        + String.valueOf(umShareContentEntity.getScore()) + "分，在小伙伴们排名前"
                        + Utils.rateToPercent(rank) + "%，上岸指日可待~~";
            } else if (rank < 0.5 && rank >= 0.25) {
                // 50%-25%
                return "学习Day" + String.valueOf(umShareContentEntity.getLearningDays())
                        + "，我的" + umShareContentEntity.getExamName() + "考试已经刷到了"
                        + String.valueOf(umShareContentEntity.getScore()) + "分，在小伙伴们排名前"
                        + Utils.rateToPercent(rank) + "%，成公就在眼前啦~";
            } else {
                // 25%-1%
                return "学习Day" + String.valueOf(umShareContentEntity.getLearningDays())
                        + "，我的" + umShareContentEntity.getExamName() + "考试已经刷到了"
                        + String.valueOf(umShareContentEntity.getScore()) + "分，在小伙伴们排名前"
                        + Utils.rateToPercent(rank) + "%，排名靠前也是很孤独的，谁来打败我啊？";
            }

        } else if ("measure_analysis".equals(umShareContentEntity.getType())) {
            // 单题解析
            String[] content = {"检验学霸的唯一标准就是做对题目，我出一道考考你？接招吗？",
                                "发现一道比较难的题目，我可是做对了哦，你呢？",
                                "长得美的这道题都做对了，比如我~~",
                                "这道题我用时不到一分钟哦，看看你是不是比我快？"};
            int random = new Random().nextInt(content.length);
            return content[random];
        }

        return "";
    }

    /**
     * 获取友盟跳转链接
     * @param urlEntity 实体类
     * @return 地址
     */
    public static String getUrl(UMShareUrlEntity urlEntity) {
        if (urlEntity == null) return "";

        if ("practice_report".equals(urlEntity.getType())) {

        }

        return "";
    }
}
