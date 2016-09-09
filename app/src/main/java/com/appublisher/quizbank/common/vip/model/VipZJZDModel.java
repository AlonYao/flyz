package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.appublisher.lib_login.model.business.LoginModel;

/**
 * 小班：字迹诊断模块
 */
public class VipZJZDModel extends VipManager{

    private static final int PIC_SIDE = 147;
    private String mPicName = "ZJZD_" + LoginModel.getUserId() + ".jpg";

    public VipZJZDModel(Context context) {
        super(context);
    }

    /**
     * 跳转至拍照或相册
     */
    public void toCamera() {
        toCamera(1);
    }

    /**
     * 获取缩略图
     * @param data 图片地址
     * @return Bitmap
     */
    public Bitmap getThumbnail(Intent data) {
        return getThumbnail(data, 0, PIC_SIDE, PIC_SIDE);
    }

}
