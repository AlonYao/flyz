package com.appublisher.quizbank.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.activity.EvaluationActivity;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.activity.PracticeReportActivity;
import com.appublisher.quizbank.dao.GlobalSettingDAO;
import com.appublisher.quizbank.dao.GradeDAO;
import com.appublisher.quizbank.model.business.EvaluationModel;
import com.appublisher.quizbank.model.business.PracticeReportModel;
import com.appublisher.quizbank.model.entity.umeng.UMShareContentEntity;
import com.appublisher.quizbank.model.entity.umeng.UMShareUrlEntity;
import com.appublisher.quizbank.model.entity.umeng.UmengShareEntity;
import com.appublisher.quizbank.model.netdata.globalsettings.GlobalSettingsResp;
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
     *
     * @param context  引用
     * @param event_id 事件id
     * @param param    事件Action
     * @param desc     事件描述
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
     *
     * @param context  引用
     * @param event_id 事件id
     * @param map      事件Map
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
     *
     * @param entry param-起
     * @param done  param-终
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
     *
     * @param activity MeasureActivity
     * @param done     离开练习的状态
     */
    public static void sendToUmeng(MeasureActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(练习解析页面)
     *
     * @param activity MeasureActivity
     * @param done     离开练习的状态
     */
    public static void sendToUmeng(MeasureAnalysisActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mAnalysisType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(答题卡页面)
     *
     * @param activity AnswerSheetActivity
     * @param done     离开练习的状态
     */
    public static void sendToUmeng(AnswerSheetActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }

    /**
     * 练习统计发送到Umeng(练习报告页面)
     *
     * @param activity PracticeReportActivity
     * @param done     离开练习的状态
     */
    public static void sendToUmeng(PracticeReportActivity activity, String done) {
        long dur = System.currentTimeMillis() - activity.mUmengTimestamp;
        HashMap<String, String> map = UmengManager.umengMeasureMap(activity.mUmengEntry, done);
        UmengManager.sendComputeEvent(activity, activity.mPaperType, map, (int) (dur / 1000));
    }

    /**
     * 打开分享列表
     *
     * @param umengShareEntity 友盟分享实体类
     */
    public static void openShare(final UmengShareEntity umengShareEntity) {
        if (umengShareEntity == null) return;

        mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);

        final Activity activity = umengShareEntity.getActivity();

        // 初始化标题
        String title = "我的天天模考";

        // 初始化文字
        String content = umengShareEntity.getContent();
        if (content == null || content.length() == 0)
            content = activity.getString(R.string.app_name);
        // 初始化跳转地址
        String url = umengShareEntity.getUrl() == null ? "" : umengShareEntity.getUrl();

        // 初始化图片
        UMImage umImage = new UMImage(activity, R.drawable.umeng_share);

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
        weixin.setTitle(title);
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
        weixinCircle.setTitle(title);
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
        qq.setTitle(title);
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
        qzone.setTitle(title);
        mController.setShareMedia(qzone);

        // 新浪微博
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        SinaShareContent sina = new SinaShareContent();

        sina.setTitle(title);

        // 新浪微博文字部分---获取ios&Android的下载地址
        String ios = "";
        String android = "";
        GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
        if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
            ios = globalSettingsResp.getApp_ios_url();
            android = globalSettingsResp.getApp_android_url();
        }

        // 新浪微博文字部分---单题解析特殊处理
        if ("measure_analysis".equals(umengShareEntity.getFrom())) {
            content = content + "戳右看看：";
        }

        sina.setShareContent("#天天模考#" + content + url + "(分享自@腰果公务员)ios："
                + ios + "安卓：" + android + "#公考要过，就用腰果#");

        // 新浪微博图片部分---特殊处理
        Bitmap bitmap = umengShareEntity.getBitmap();
        if (!"measure_analysis".equals(umengShareEntity.getFrom()) && bitmap != null) {
            umImage = new UMImage(activity, bitmap);
        } else {
            umImage = new UMImage(activity, R.drawable.umeng_share);
        }
        sina.setShareImage(umImage);

        mController.setShareMedia(sina);

        // 友盟分享监听
        mController.getConfig().cleanListeners();
        mController.openShare(activity, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {
                ToastManager.showToast(activity, "分享中……");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media,
                                   int i,
                                   SocializeEntity socializeEntity) {
                /** Umeng统计 **/

                // 来源
                String type = null;
                if ("measure_analysis".equals(umengShareEntity.getFrom())) {
                    type = "0";
                } else if ("practice_report".equals(umengShareEntity.getFrom())) {
                    type = "1";
                } else if ("evaluation".equals(umengShareEntity.getFrom())) {
                    type = "2";
                }

                // 是否成功
                String action = "0";
                if (i == 200) action = "1";

                // 发送到Umeng
                HashMap<String, String> map = new HashMap<>();
                map.put("Type", type);
                map.put("Action", action);
                map.put("SNS", share_media.name());
                MobclickAgent.onEvent(umengShareEntity.getActivity(), "Share", map);
            }
        });
    }

    /**
     * 获取分享文字
     *
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
                        + "考试可以估分了呢，我的预测分是"
                        + umShareContentEntity.getScore()
                        + "分，小伙伴们快来看看~";
            } else if ("mock".equals(umShareContentEntity.getPaperType())) {
                return "我在考试"
                        + umShareContentEntity.getExamName()
                        + "中拿了"
                        + umShareContentEntity.getScore()
                        + "分，上考场前不模考几次怎么行！";
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
     *
     * @param urlEntity 实体类
     * @return 地址
     */
    public static String getUrl(UMShareUrlEntity urlEntity) {
        if (urlEntity == null) return "";

        if ("practice_report".equals(urlEntity.getType())) {
            // 练习报告
            GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
            String baseUrl = "http://m.zhiboke.net/#/live/practiceReport?";
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                baseUrl = globalSettingsResp.getReport_share_url();
            }
            return baseUrl + "user_id=" + urlEntity.getUser_id()
                    + "&user_token=" + urlEntity.getUser_token()
                    + "&exercise_id=" + urlEntity.getExercise_id()
                    + "&paper_type=" + urlEntity.getPaper_type()
                    + "&name=" + urlEntity.getName();

        } else if ("evaluation".equals(urlEntity.getType())) {
            // 能力评估
            GlobalSettingsResp globalSettingsResp = GlobalSettingDAO.getGlobalSettingsResp();
            String baseUrl = "http://m.zhiboke.net/#/live/assessment?";
            if (globalSettingsResp != null && globalSettingsResp.getResponse_code() == 1) {
                baseUrl = globalSettingsResp.getEvaluate_share_url();
            }
            return baseUrl + "user_id=" + urlEntity.getUser_id()
                    + "&user_token=" + urlEntity.getUser_token();
        } else if ("measure_analysis".equals(urlEntity.getType())) {
            // 单题解析
            return "http://share.zhiboke.net/question.php?question_id="
                    + urlEntity.getQuestion_id();
        } else if ("course_detail".equals(urlEntity.getType())) {
            return "http://m.yaoguo.cn/terminalType/shareCourse.html?course_id=" + urlEntity.getCourse_id();
        }

        return "";
    }

    /**
     * 检查当天是否进行友盟分享
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    public static void checkUmengShare(Activity activity) {
        // 获取上次记录的离开日期
        String firstLeaveDate = GradeDAO.getFirstLeaveDate(Globals.appVersion, activity);

        if (firstLeaveDate != null && firstLeaveDate.equals(Utils.getCurDate())) {
            // 如果是同一天发生的
            if (activity instanceof PracticeReportActivity)
                // 如果是PracticeReportActivity，额外需要传送Umeng统计的数据
                UmengManager.sendToUmeng((PracticeReportActivity) activity, "Back");

            activity.finish();

        } else {
            // 如果是当前的第一次
            showEveryDayShareAlert(activity);
        }

        // 更新离开日期
        GradeDAO.updateFirstLeaveDate(Globals.appVersion, Utils.getCurDate(), activity);
    }

    /**
     * 展示每天友盟分享提醒Alert
     *
     * @param activity EvaluationActivity：能力评估页 PracticeReportActivity：练习报告页
     */
    private static void showEveryDayShareAlert(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.grade_everydayshare_alert_msg)
                .setTitle(R.string.alert_title)
                .setPositiveButton(R.string.grade_everydayshare_alert_p,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (activity instanceof EvaluationActivity) {
                                    EvaluationModel.setUmengShare((EvaluationActivity) activity);
                                } else if (activity instanceof PracticeReportActivity) {
                                    PracticeReportModel.setUmengShare(
                                            (PracticeReportActivity) activity);
                                }

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.grade_everydayshare_alert_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if (activity instanceof PracticeReportActivity)
                                    // 如果是PracticeReportActivity，额外需要传送Umeng统计的数据
                                    UmengManager.sendToUmeng(
                                            (PracticeReportActivity) activity, "Back");

                                activity.finish();
                            }
                        })
                .create().show();
    }
}
