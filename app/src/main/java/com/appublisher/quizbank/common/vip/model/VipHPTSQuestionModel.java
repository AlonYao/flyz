package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.widget.ImageView;

import com.appublisher.quizbank.common.vip.activity.VipHPTSActivity;

/**
 * 小班：互评提升 问题Tab
 */

public class VipHPTSQuestionModel extends VipHPTSModel {

    public boolean mCanSubmit;
    public int mQuestionId;

    private VipHPTSActivity mView;

    public VipHPTSQuestionModel(Context context) {
        super(context);
        mView = (VipHPTSActivity) context;
    }

    public ImageView getMyJobItem() {
        return mView.getMyJobItem();
    }

    public void submit(int recordId, String postil, String level) {
        VipSubmitEntity entity = new VipSubmitEntity();
        entity.exercise_id = mExerciseId;
        entity.question_id = mQuestionId;
        entity.record_id = recordId;
        entity.postil = postil;
        entity.level = level;
        mView.showLoading();
        submit(entity);
    }
}
