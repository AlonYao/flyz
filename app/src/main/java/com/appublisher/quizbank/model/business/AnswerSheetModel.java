package com.appublisher.quizbank.model.business;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.appublisher.lib_basic.ProgressDialogManager;
import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.customui.ExpandableHeightGridView;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.AnswerSheetActivity;
import com.appublisher.quizbank.adapter.EntireAnswerSheetAdapter;
import com.appublisher.quizbank.model.netdata.ServerCurrentTimeResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.QRequest;
import com.appublisher.quizbank.utils.AlertManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * AnswerSheet Activity Model
 */
public class AnswerSheetModel {

    /**
     * 设置整卷答题卡
     *
     * @param activity AnswerSheetActivity
     */
    @SuppressWarnings({"unchecked", "Annotator"})
    public static void setEntireContent(final AnswerSheetActivity activity) {
        if (activity.mUserAnswerList == null
                || activity.mUserAnswerList.size() == 0
                || activity.mEntirePaperCategory == null) return;

        int offset = 0;
        int categoryNum = 1;
        int size = activity.mEntirePaperCategory.size();

        for (int i = 0; i < size; i++) {
            HashMap<String, Integer> map = activity.mEntirePaperCategory.get(i);

            if (map == null) continue;

            String categoryName = null;
            for (String key : map.keySet()) {
                categoryName = key;
            }

            int categorySize = map.get(categoryName);

            View categoryChildView =
                    LayoutInflater.from(activity).inflate(
                            R.layout.answer_sheet_entire_item,
                            activity.mLlEntireContainer,
                            false);

            TextView tvName =
                    (TextView) categoryChildView.findViewById(R.id.answer_sheet_entire_item_tv);
            ExpandableHeightGridView ehGridView =
                    (ExpandableHeightGridView)
                            categoryChildView.findViewById(R.id.answer_sheet_entire_item_gv);

            String text = "第" + String.valueOf(categoryNum) + "部分  " + categoryName;
            tvName.setText(text);

            // 构造分类的题目
            ArrayList<HashMap<String, Object>> categoryUserAnswer = new ArrayList<>();

            for (int j = offset; j < offset + categorySize; j++) {
                categoryUserAnswer.add(activity.mUserAnswerList.get(j));
            }

            EntireAnswerSheetAdapter entireAnswerSheetAdapter =
                    new EntireAnswerSheetAdapter(activity, categoryUserAnswer, offset);
            ehGridView.setAdapter(entireAnswerSheetAdapter);

            activity.mLlEntireContainer.addView(categoryChildView);

            offset = offset + categorySize;
            categoryNum++;
        }
    }

    /**
     * 提交答案
     *
     * @param activity AnswerSheetActivity
     */
    public static void submitPaper(AnswerSheetActivity activity) {
        // 重置数据
        activity.mRightNum = 0;
        int duration_total = 0;
        HashMap<String, Object> userAnswerMap;
        JSONArray questions = new JSONArray();

        boolean redo = activity.getIntent().getBooleanExtra("redo", false);
        // 标记有没有未做的题
        boolean hasNoAnswer = false;

        String redoSubmit;
        if (redo) {
            redoSubmit = "true";
        } else {
            redoSubmit = "false";
        }

        activity.mCategoryMap = new HashMap<>();

        if (activity.mUserAnswerList == null) return;

        activity.mTotalNum = activity.mUserAnswerList.size();
        for (int i = 0; i < activity.mTotalNum; i++) {
            try {
                userAnswerMap = activity.mUserAnswerList.get(i);

                int id = (int) userAnswerMap.get("id");
                String answer = (String) userAnswerMap.get("answer");
                boolean is_right = false;
                int category = (int) userAnswerMap.get("category_id");
                String category_name = (String) userAnswerMap.get("category_name");
                int note_id = (int) userAnswerMap.get("note_id");
                int duration = (int) userAnswerMap.get("duration");
                String right_answer = (String) userAnswerMap.get("right_answer");

                // 判断对错
                if (answer != null && right_answer != null
                        && !"".equals(answer) && answer.equals(right_answer)) {
                    is_right = true;
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
                joQuestion.put("note_id", note_id);
                joQuestion.put("duration", duration);
                questions.put(joQuestion);

                // 统计科目信息
                if (category_name != null
                        && activity.mCategoryMap.containsKey(category_name)) {
                    // 更新Map
                    HashMap<String, Object> map = activity.mCategoryMap.get(category_name);

                    int medium;

                    // 正确题目的数量
                    if (is_right) {
                        medium = (int) map.get("right_num");
                        medium++;
                        map.put("right_num", medium);
                    }

                    // 总数
                    medium = (int) map.get("total_num");
                    medium++;
                    map.put("total_num", medium);

                    // 总时长
                    medium = (int) map.get("duration_total");
                    medium = medium + duration;
                    map.put("duration_total", medium);

                    // 保存
                    activity.mCategoryMap.put(category_name, map);
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    if (is_right) {
                        map.put("right_num", 1);
                    } else {
                        map.put("right_num", 0);
                    }
                    map.put("total_num", 1);
                    map.put("duration_total", duration);
                    activity.mCategoryMap.put(category_name, map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (hasNoAnswer) {
            // 提示用户是否提交
            AlertManager.answerSheetNoticeAlert(activity, redoSubmit, duration_total, questions);
        } else {
            ProgressDialogManager.showProgressDialog(activity, false);
            new QRequest(activity, activity).submitPaper(
                    ParamBuilder.submitPaper(
                            String.valueOf(activity.mPaperId),
                            String.valueOf(activity.mPaperType),
                            redoSubmit,
                            String.valueOf(duration_total),
                            questions.toString(),
                            "done")
            );
        }
    }

    /**
     * 处理服务器时间回调
     * @param resp ServerCurrentTimeResp
     * @param activity AnswerSheetActivity
     */
    public static void dealServerCurrentTimeResp(ServerCurrentTimeResp resp,
                                                 AnswerSheetActivity activity) {
        if (resp == null || resp.getResponse_code() != 1) return;

        String serverTime = resp.getCurrent_time();
        if (serverTime == null || serverTime.length() == 0) return;

        if (activity.mMockTime == null || activity.mMockTime.length() == 0) return;

        long seconds = 0;

        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition parsePosition = new ParsePosition(0);
            Date time = formatter.parse(activity.mMockTime, parsePosition);
            seconds = time.getTime() - Long.parseLong(serverTime) * 1000;
        } catch (Exception e) {
            // Empty
        }

        if (seconds / 1000 > -(30 * 60)) {
            ToastManager.showToast(activity, "开考30分钟后才可以交卷");
        } else {
            AnswerSheetModel.submitPaper(activity);
        }
    }
}
