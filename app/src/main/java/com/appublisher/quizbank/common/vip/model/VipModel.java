package com.appublisher.quizbank.common.vip.model;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.common.vip.network.VipParamBuilder;
import com.appublisher.quizbank.common.vip.network.VipRequest;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 小班模块管理类
 */
public class VipManager {

    /**
     * 小班系统真题作业提交
     * @param activity AnswerSheetActivity
     */
    public static void vipSubmitPaper(AnswerSheetActivity activity) {
        if (activity.mUserAnswerList == null) return;

        // 初始化数据
        int duration_total = 0;

        JSONArray questions = new JSONArray();

        // 标记有没有未做的题
        boolean hasNoAnswer = false;

        HashMap<String, Object> userAnswerMap;
        for (int i = 0; i < activity.mTotalNum; i++) {
            try {
                userAnswerMap = activity.mUserAnswerList.get(i);

                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                int category = (int) userAnswerMap.get("category_id");
                String category_name = (String) userAnswerMap.get("category_name");
                int note_id = (int) userAnswerMap.get("note_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");
                //noinspection unchecked
                ArrayList<Integer> note_ids = (ArrayList<Integer>) userAnswerMap.get("note_ids");

                // 判断对错
                int is_right = 0;
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = 1;
                    activity.mRightNum++;
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
//                joQuestion.put("note_id", note_id);
                joQuestion.put("note_ids", note_ids);
                joQuestion.put("duration", duration);
                questions.put(joQuestion);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (hasNoAnswer) {
            // 提示用户存在未完成课程
//            vipUnFinishAlert(activity, redoSubmit, duration_total, questions);
        } else {
            ProgressDialogManager.showProgressDialog(activity, false);
//            new QRequest(activity, activity).submitPaper(
//                    ParamBuilder.submitPaper(
//                            String.valueOf(activity.mPaperId),
//                            String.valueOf(activity.mPaperType),
//                            redoSubmit,
//                            String.valueOf(duration_total),
//                            questions.toString(),
//                            "done")
//            );
//            new VipRequest(activity).submit(
//                    VipParamBuilder.submit(
//                            activity.mPaperId,
//                            "",
//                            )
//            );
        }
    }

//    /**
//     * 如果有未完成题目时的提示
//     *
//     * @param activity       AnswerSheetActivity
//     * @param redoSubmit     是否是重做
//     * @param duration_total 总做题时间
//     * @param questions      用户答案信息
//     */
//    public static void vipUnFinishAlert(final AnswerSheetActivity activity,
//                                        final JSONArray questions) {
//        new AlertDialog.Builder(activity)
//                .setMessage(R.string.alert_answersheet_content)
//                .setTitle(R.string.alert_logout_title)
//                .setPositiveButton(R.string.alert_answersheet_p,
//                        new DialogInterface.OnClickListener() {// 确定
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                ProgressDialogManager.showProgressDialog(activity, false);
//                                new QRequest(activity, activity).submitPaper(
//                                        ParamBuilder.submitPaper(
//                                                String.valueOf(activity.mPaperId),
//                                                String.valueOf(activity.mPaperType),
//                                                redoSubmit,
//                                                String.valueOf(duration_total),
//                                                questions.toString(),
//                                                "done")
//                                );
//
//                                dialog.dismiss();
//                            }
//                        })
//                .setNegativeButton(R.string.alert_answersheet_n,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                .create().show();
//    }

}
