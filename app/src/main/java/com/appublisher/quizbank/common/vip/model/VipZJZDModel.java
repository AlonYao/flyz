package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
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

    public void toCamera() {
        toCamera(mPicName);
    }

    public Bitmap getThumbnail() {
        return getThumbnail(mPicName, PIC_SIDE, PIC_SIDE);
    }

}
