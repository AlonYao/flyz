package com.appublisher.quizbank.common.vip.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipXCReportActivity;
import com.appublisher.quizbank.common.vip.netdata.VipSubmitResp;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 小班：行测模块
 */
public class VipXCModel extends VipBaseModel {

    private IntelligentPaperListener mIntelligentPaperListener;
    private int mExerciseId;

    public VipXCModel(Context context) {
        super(context);
    }

    /**
     * 智能组卷接口
     */
    public interface IntelligentPaperListener {
        void complete(JSONObject resp);
    }

    /**
     * 获取智能组卷
     * @param listener IntelligentPaperListener
     * @param exercise_id 练习id
     */
    public void obtainIntelligentPaper(int exercise_id, IntelligentPaperListener listener) {
        mIntelligentPaperListener = listener;
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).showLoading();
        mVipRequest.getIntelligentPaper(exercise_id);
    }

    /**
     * 处理智能组卷回调
     * @param response JSONObject
     */
    private void dealIntelligentPaperResp(JSONObject response) {
        mIntelligentPaperListener.complete(response);
    }

    /**
     * 小班系统真题作业提交
     */
    public void submitPaper(ArrayList<HashMap<String, Object>> list,
                            int exercise_id) {
        if (list == null) return;

        // 初始化数据
        int duration_total = 0;
        mExerciseId = exercise_id;

        JSONArray answers = new JSONArray();

        HashMap<String, Object> userAnswerMap;
        for (int i = 0; i < list.size(); i++) {
            try {
                userAnswerMap = list.get(i);
                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                int category = (int) userAnswerMap.get("category_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");
                //noinspection unchecked
                ArrayList<Integer> noteIdsList = (ArrayList<Integer>) userAnswerMap.get("note_ids");
                JSONArray note_ids = new JSONArray(noteIdsList);
                // 判断对错
                int is_right = 0;
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = 1;
                }
                // 统计总时长
                duration_total = duration_total + duration;
                // 构造提交数据结构
                JSONObject joQuestion = new JSONObject();
                joQuestion.put("id", id);
                joQuestion.put("answer", answer);
                joQuestion.put("is_right", is_right);
                joQuestion.put("category", category);
                joQuestion.put("note_ids", note_ids);
                joQuestion.put("duration", duration);
                answers.put(joQuestion);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        postPaperAnswer(mContext, exercise_id, answers.toString(), duration_total);
    }

    /**
     * 提交真题答案
     * @param context Context
     * @param exercise_id 练习id
     * @param answers 用户答案
     * @param duration 总时长
     */
    private void postPaperAnswer(Context context,
                                 int exercise_id,
                                 String answers,
                                 int duration) {
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).showLoading();
        VipSubmitEntity entity = new VipSubmitEntity();
        entity.exercise_id = exercise_id;
        entity.answer_content = answers;
        entity.duration = duration;

        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).showLoading();
        new VipRequest(context, this).submit(VipParamBuilder.submit(entity));
    }

    /**
     * 如果有未完成题目时的提示
     */
    public void showUnFinishAlert(final Context context,
                                  final int exercise_id,
                                  final String answers,
                                  final int duration) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.alert_answersheet_content)
                .setTitle(R.string.alert_logout_title)
                .setPositiveButton(R.string.alert_answersheet_p,
                        new DialogInterface.OnClickListener() {// 确定

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postPaperAnswer(
                                        context,
                                        exercise_id,
                                        answers,
                                        duration);
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_answersheet_n,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    private void dealSubmitResp(JSONObject response) {
        VipSubmitResp resp = GsonManager.getModel(response, VipSubmitResp.class);
        if (resp == null || resp.getResponse_code() != 1) {
            Toast.makeText(mContext, "提交失败……", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mContext, VipXCReportActivity.class);
            intent.putExtra("exerciseId", mExerciseId);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.GET_INTELLIGENT_PAPER.equals(apiName)) {
            dealIntelligentPaperResp(response);
        } else if (VipRequest.SUBMIT.equals(apiName)) {
            dealSubmitResp(response);
        }
        super.requestCompleted(response, apiName);
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        super.requestCompleted(response, apiName);
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        super.requestEndedWithError(error, apiName);
        if (mContext instanceof BaseActivity) ((BaseActivity) mContext).hideLoading();
    }
}
