package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.ActivitySkipConstants;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.AnswerSheetAdapter;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.netdata.measure.NoteM;
import com.appublisher.quizbank.model.netdata.measure.SubmitPaperResp;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 答题卡
 */
public class AnswerSheetActivity extends ActionBarActivity implements RequestCallback{

    private Gson mGson;
    private String mPaperName;
    private int mRightNum;
    private int mTotalNum;
    private HashMap<String, HashMap<String, Object>> mCategoryMap;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_sheet);

        // Toolbar
        CommonModel.setToolBar(this);

        // View 初始化
        GridView gridView = (GridView) findViewById(R.id.answer_sheet_gv);
        TextView tvSubmit = (TextView) findViewById(R.id.answer_sheet_submit);

        // 成员变量初始化
        mGson = new Gson();

        // 获取数据
        final ArrayList<HashMap<String, Object>> userAnswerList =
                (ArrayList<HashMap<String, Object>>)
                        getIntent().getSerializableExtra("user_answer");
        final String paperType = getIntent().getStringExtra("paper_type");
        final int paperId = getIntent().getIntExtra("paper_id", 0);
        final boolean redo = getIntent().getBooleanExtra("redo", false);
        mPaperName = getIntent().getStringExtra("paper_name");

        if (userAnswerList != null) {
            AnswerSheetAdapter answerSheetAdapter = new AnswerSheetAdapter(this, userAnswerList);
            gridView.setAdapter(answerSheetAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(AnswerSheetActivity.this, MeasureActivity.class);
                    intent.putExtra("position", position);
                    setResult(ActivitySkipConstants.ANSWER_SHEET_SKIP, intent);
                    finish();
                }
            });
        }

        // 交卷
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration_total = 0;
                HashMap<String, Object> userAnswerMap;
                JSONArray questions = new JSONArray();

                String redoSubmit;
                if (redo) {
                    redoSubmit = "true";
                } else {
                    redoSubmit = "false";
                }

                mCategoryMap = new HashMap<>();

                if (userAnswerList == null) return;

                mTotalNum = userAnswerList.size();
                for (int i = 0; i < mTotalNum; i++) {
                    try {
                        userAnswerMap = userAnswerList.get(i);

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
                            mRightNum++;
                        }

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
                                && mCategoryMap.containsKey(category_name)) {
                            // 更新Map
                            HashMap<String, Object> map = mCategoryMap.get(category_name);

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
                            mCategoryMap.put(category_name, map);
                        } else {
                            HashMap<String, Object> map = new HashMap<>();
                            if (is_right) {
                                map.put("right_num", 1);
                            } else {
                                map.put("right_num", 0);
                            }
                            map.put("total_num", 1);
                            map.put("duration_total", duration);
                            mCategoryMap.put(category_name, map);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                ProgressDialogManager.showProgressDialog(AnswerSheetActivity.this, false);
                new Request(AnswerSheetActivity.this, AnswerSheetActivity.this).submitPaper(
                        ParamBuilder.submitPaper(
                                String.valueOf(paperId),
                                String.valueOf(paperType),
                                redoSubmit,
                                String.valueOf(duration_total),
                                questions.toString(),
                                "done")
                );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理提交试卷的回调
     * @param response 回调
     */
    private void dealSubmitPaperResp(JSONObject response) {
        if (response == null) return;

        Logger.i(response.toString());

        SubmitPaperResp submitPaperResp =
                mGson.fromJson(response.toString(), SubmitPaperResp.class);

        if (submitPaperResp == null || submitPaperResp.getResponse_code() != 1) return;

        ArrayList<NoteM> notes = submitPaperResp.getNotes();

        Intent intent = new Intent(AnswerSheetActivity.this, MeasureActivity.class);
        intent.putExtra("notes", notes);
        intent.putExtra("paper_name", mPaperName);
        intent.putExtra("right_num", mRightNum);
        intent.putExtra("total_num", mTotalNum);
        intent.putExtra("category", mCategoryMap);
        setResult(ActivitySkipConstants.ANSWER_SHEET_SUBMIT, intent);
        finish();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if ("submit_paper".equals(apiName)) {
            dealSubmitPaperResp(response);
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }
}
