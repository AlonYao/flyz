package com.appublisher.quizbank.common.vip.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
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
        ProgressDialogManager.showProgressDialog(mContext);
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

        JSONArray answers = new JSONArray();

        // 标记有没有未做的题
        boolean hasNoAnswer = false;

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
                ArrayList<Integer> note_ids = (ArrayList<Integer>) userAnswerMap.get("note_ids");

                // 判断对错
                int is_right = 0;
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = 1;
                }

                // 标记有没有未做的题
                if (answer == null || answer.length() == 0) hasNoAnswer = true;

                // 统计总时长
                duration_total = duration_total + duration;

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

        if (hasNoAnswer) {
            // 提示用户存在未完成课程
            showUnFinishAlert(mContext, exercise_id, answers.toString(), duration_total);
        } else {
            postPaperAnswer(mContext, exercise_id, answers.toString(), duration_total);
        }
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
        ProgressDialogManager.showProgressDialog(context, false);
        VipSubmitEntity entity = new VipSubmitEntity();
        entity.exercise_id = exercise_id;
        entity.answer_content = answers;
        entity.duration = duration;
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

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (VipRequest.GET_INTELLIGENT_PAPER.equals(apiName)) {
            dealIntelligentPaperResp(response);
        }
        super.requestCompleted(response, apiName);
    }
}
