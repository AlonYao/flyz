package com.appublisher.quizbank.model.login.model;

import android.os.Bundle;
import android.view.View;

import com.appublisher.quizbank.model.login.activity.UserInfoActivity;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.util.Map;

/**
 * 个人设置模型
 */
public class UserInfoSetModel {

    private UserInfoActivity mActivity;

    /**
     * 构造函数
     * @param activity  登录Activity
     */
    public UserInfoSetModel(UserInfoActivity activity) {
        this.mActivity = activity;
    }

    /**
     * 微信点击事件
     */
    public View.OnClickListener weixinOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.mController.doOauthVerify(mActivity, SHARE_MEDIA.WEIXIN,
                    new SocializeListeners.UMAuthListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {
                            ProgressDialogManager.showProgressDialog(mActivity, false);
                        }

                        @Override
                        public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                            mActivity.mController.getPlatformInfo(mActivity, SHARE_MEDIA.WEIXIN,
                                    new SocializeListeners.UMDataListener() {
                                        @Override
                                        public void onStart() {
                                            // 获取数据开始
                                        }

                                        @Override
                                        public void onComplete(int status, Map<String, Object> info) {
                                            if(status == 200 && info != null){
                                                String login_id = info.get("unionid").toString();
                                                mActivity.mRequest.authHandle(ParamBuilder.authHandle(
                                                        "2",
                                                        "add",
                                                        login_id,
                                                        ""));
                                                mActivity.mType = "weixin";

                                            }else{
                                                ProgressDialogManager.closeProgressDialog();
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onError(SocializeException e, SHARE_MEDIA share_media) {
                            ProgressDialogManager.closeProgressDialog();
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {
                            ProgressDialogManager.closeProgressDialog();
                        }
                    });
        }
    };

    /**
     * 微博点击事件
     */
    public View.OnClickListener weiboOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.mController.doOauthVerify(mActivity, SHARE_MEDIA.SINA,
                    new SocializeListeners.UMAuthListener() {
                        @Override
                        public void onStart(SHARE_MEDIA share_media) {
                            ProgressDialogManager.showProgressDialog(mActivity, false);
                        }

                        @Override
                        public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                            mActivity.mController.getPlatformInfo(mActivity, SHARE_MEDIA.SINA,
                                    new SocializeListeners.UMDataListener() {
                                        @Override
                                        public void onStart() {
                                            // 获取平台数据开始
                                        }
                                        @Override
                                        public void onComplete(int status, Map<String, Object> info) {
                                            if(status == 200 && info != null){
                                                String login_id = info.get("uid").toString();

                                                mActivity.mRequest.authHandle(ParamBuilder.authHandle(
                                                        "1",
                                                        "add",
                                                        login_id,
                                                        ""));
                                                mActivity.mType = "weibo";
                                            }else{
                                                ProgressDialogManager.closeProgressDialog();
                                            }
                                        }
                                    });
                        }

                        @Override
                        public void onError(SocializeException e, SHARE_MEDIA share_media) {
                            ProgressDialogManager.closeProgressDialog();
                        }

                        @Override
                        public void onCancel(SHARE_MEDIA share_media) {
                            ProgressDialogManager.closeProgressDialog();
                        }
                    });
        }
    };
}
