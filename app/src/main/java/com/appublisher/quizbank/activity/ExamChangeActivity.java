package com.appublisher.quizbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.adapter.ExamListAdapter;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.netdata.UserExamInfoModel;
import com.appublisher.quizbank.model.netdata.exam.ExamDetailModel;
import com.appublisher.quizbank.model.netdata.exam.ExamItemModel;
import com.appublisher.quizbank.model.netdata.exam.ExamSetResponseModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 考试项目选择Activity
 */
public class ExamChangeActivity extends ActionBarActivity implements RequestCallback{

    private ListView mLv;
    private Gson mGson;
    private List<ExamItemModel> mExams;
    private LinearLayout mRlSelected;
    private TextView mTvSelected;
    private Request mRequest;
    private ExamItemModel mCurExamItem;
    private String mFrom;
    private TextView mTvConfirm;
    private boolean mPreExamStatus;
    private int mPreExamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_change);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        mLv = (ListView) findViewById(R.id.exam_change_lv);
        mRlSelected = (LinearLayout) findViewById(R.id.exam_change_selected);
        mTvSelected = (TextView) findViewById(R.id.exam_change_selected_tv);
        mTvConfirm = (TextView) findViewById(R.id.examchange_confirm);

        // 成员变量初始化
        mGson = GsonManager.initGson();
        mRequest = new Request(this, this);
        mFrom = getIntent().getStringExtra("from");
        if (mFrom == null) mFrom = "";
        mPreExamStatus = LoginModel.hasExamInfo();

        // 获取数据
        ProgressDialogManager.showProgressDialog(this, true);
        mRequest.getExamList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Umeng
        MobclickAgent.onPageStart("ExamChangeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 开启计划的放弃点统计
        if (!LoginModel.hasExamInfo()) {
            // 如果没有考试项目
            HashMap<String,String> map = new HashMap<>();
            map.put("Action", "0");
            MobclickAgent.onEvent(this, "SetPlan", map);
        } else if (!mPreExamStatus && LoginModel.hasExamInfo()) {
            // 之前没有考试项目，结束的时候有考试项目
            HashMap<String,String> map = new HashMap<>();
            map.put("Action", "1");
            MobclickAgent.onEvent(this, "SetPlan", map);
        }

        // Umeng
        MobclickAgent.onPageEnd("ExamChangeActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 检查是否设置了考试项目
            checkExam();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // 检查是否设置了考试项目
        checkExam();
    }

    /**
     * 检查是否设置了考试项目
     */
    private void checkExam() {
        if ("login".equals(mFrom) && !LoginModel.hasExamInfo()) {
            ToastManager.showToast(this, "请选择考试项目");
        } else {
            finish();
        }
    }

    /**
     * 设置内容
     */
    private void setContent() {
        if (mExams == null || mExams.size() == 0) return;

        final ExamListAdapter examListAdapter = new ExamListAdapter(this, mExams);
        mLv.setAdapter(examListAdapter);

        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurExamItem = mExams.get(position);
                String name = mCurExamItem.getName();

                mRlSelected.setVisibility(View.VISIBLE);
                mTvSelected.setText(name);

                examListAdapter.setSelectedPosition(position);
                examListAdapter.notifyDataSetChanged();

                // 提交按钮颜色变化
                if (mPreExamId != mCurExamItem.getExam_id()) {
                    mTvConfirm.setBackgroundResource(R.color.exam_change_button);
                } else {
                    mTvConfirm.setBackgroundResource(R.color.practice_report_error_gray);
                }
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurExamItem == null) {
                    ToastManager.showToast(ExamChangeActivity.this, "请选择考试项目");
                    return;
                } else if (mPreExamId == mCurExamItem.getExam_id()) {
                    ToastManager.showToast(ExamChangeActivity.this, "您的考试项目没有变化");
                    return;
                }

                int id = mCurExamItem.getExam_id();

                ProgressDialogManager.showProgressDialog(ExamChangeActivity.this, false);
                mRequest.setExam(ParamBuilder.setExam(String.valueOf(id)));
            }
        });

        // 添加已选择的考试项目
        UserExamInfoModel exam = LoginModel.getExamInfo();
        mPreExamId = 0;

        if (exam != null) {
            mRlSelected.setVisibility(View.VISIBLE);
            mTvSelected.setText(exam.getName());
            mPreExamId = exam.getExam_id();
        }
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response != null) {
            if (apiName.equals("exam_list")) {
                ExamDetailModel exam = mGson.fromJson(response.toString(), ExamDetailModel.class);

                if (exam.getResponse_code() == 1) {
                    mExams = exam.getExams();
                    setContent();
                }
            }

            if (apiName.equals("set_exam")) {
                ExamSetResponseModel examSetResponseModel =
                        mGson.fromJson(response.toString(), ExamSetResponseModel.class);

                if (examSetResponseModel != null && examSetResponseModel.getResponse_code() == 1) {
                    if (mCurExamItem != null) {
                        // 保存考试信息至数据库
                        String exam = mGson.toJson(mCurExamItem);
                        UserDAO.updateExamInfo(exam);

                        // 保存学号信息至数据库
                        UserDAO.updateSno(examSetResponseModel.getSno());

                        // 页面跳转
                        if ("splash".equals(mFrom) || "login".equals(mFrom)) {
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        }

                        finish();
                    }
                }
            }
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
