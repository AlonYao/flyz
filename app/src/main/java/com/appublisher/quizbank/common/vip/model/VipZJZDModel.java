package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * 小班：字迹诊断模块
 */
public class VipZJZDModel extends VipManager{

    private static final int PIC_SIDE = 147;

    public static final String INTENT_EXERCISEID = "exercise_id";

    private int mExerciseId;

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

    public void getExerciseDetail() {
        mVipRequest.getExerciseDetail(mExerciseId);
    }

    /** GET & SET **/

    public int getExerciseId() {
        return mExerciseId;
    }

    public void setExerciseId(int mExerciseId) {
        this.mExerciseId = mExerciseId;
    }
}
