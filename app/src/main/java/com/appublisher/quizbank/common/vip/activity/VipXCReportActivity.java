package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.LegacyMeasureAnalysisActivity;
import com.appublisher.quizbank.common.vip.model.VipXCReportModel;
import com.appublisher.quizbank.model.netdata.measure.AnswerM;
import com.appublisher.quizbank.model.netdata.measure.QuestionM;

import java.util.ArrayList;

public class VipXCReportActivity extends VipBaseActivity implements View.OnClickListener{

    private TextView mTvSJYS;
    private TextView mTvJYYS;
    private TextView mTvAccuracy;
    private TextView mTvSpeed;
    private TextView mTvCourseName;
    private TextView mTvExerciseName;
    private TextView mTvPosition;
    private TextView mTvAll;
    private TextView mTvError;
    private LinearLayout mLlTreeContainer;
    private VipXCReportModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_xcreport);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
        mModel = new VipXCReportModel(this);
        mModel.mExerciseId = getIntent().getIntExtra("exerciseId", 0);
        showLoading();
        mModel.getExerciseDetail();
    }

    private void initView() {
        mTvSJYS = (TextView) findViewById(R.id.vip_xc_report_sjys_value);
        mTvJYYS = (TextView) findViewById(R.id.vip_xc_report_jyys_value);
        mTvAccuracy = (TextView) findViewById(R.id.vip_xc_report_accuracy_value);
        mTvSpeed = (TextView) findViewById(R.id.vip_xc_report_speed_value);
        mTvCourseName = (TextView) findViewById(R.id.vip_xc_report_course_name);
        mTvExerciseName = (TextView) findViewById(R.id.vip_xc_report_exercise_name);
        mTvPosition = (TextView) findViewById(R.id.vip_xc_report_position);
        mTvAll = (TextView) findViewById(R.id.vip_xc_report_all);
        mTvError = (TextView) findViewById(R.id.vip_xc_report_error);
        mLlTreeContainer = (LinearLayout) findViewById(R.id.vip_xc_report_note_container);

        mTvAll.setOnClickListener(this);
        mTvError.setOnClickListener(this);
    }

    public void showCourseName(String name) {
        mTvCourseName.setText(name);
    }

    public void showExerciseName(String name) {
        mTvExerciseName.setText(name);
    }

    public void showJYYS(String time) {
        mTvJYYS.setText(time);
    }

    public void showAccuracy(String accuracy) {
        mTvAccuracy.setText(accuracy);
    }

    public void showSpeed(int speed) {
        mTvSpeed.setText(String.valueOf(speed));
    }

    public void showPosition(int position) {
        mTvPosition.setText("你是本班第" + String.valueOf(position) + "个提交作业的同学");
    }

    public LinearLayout getTreeContainer() {
        return mLlTreeContainer;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vip_xc_report_all) {
            ArrayList<QuestionM> questions = mModel.getAllQuestions();
            ArrayList<AnswerM> answers = mModel.getAllAnswers();
            Intent intent = new Intent(this, LegacyMeasureAnalysisActivity.class);
            intent.putExtra("questions", questions);
            intent.putExtra("answers", answers);
            intent.putExtra("paper_name", "");
            intent.putExtra("analysis_type", "");
            intent.putExtra("from", "vip");

            // Umeng
//            intent.putExtra("umeng_entry", mActivity.mUmengEntry);
//            intent.putExtra("umeng_timestamp", mActivity.mUmengTimestamp);
//            if ("study_record".equals(mActivity.mFrom)) {
//                intent.putExtra("umeng_entry_review", "Record");
//            } else if ("mokao_homepage".equals(mActivity.mFrom)
//                    || "mokao_history_list".equals(mActivity.mFrom)) {
//                intent.putExtra("umeng_entry_review", "MokaoList");
//            } else {
//                intent.putExtra("umeng_entry_review", "Report");
//            }

            startActivity(intent);
            finish();
        } else if (v.getId() == R.id.vip_xc_report_error) {
            ArrayList<QuestionM> questions = mModel.getErrorQuestions();
            ArrayList<AnswerM> answers = mModel.getErrorAnswers();
            Intent intent = new Intent(this, LegacyMeasureAnalysisActivity.class);
            intent.putExtra("questions", questions);
            intent.putExtra("answers", answers);
            intent.putExtra("paper_name", "");
            intent.putExtra("analysis_type", "");
            intent.putExtra("from", "vip");

            // Umeng
//            intent.putExtra("umeng_entry", mActivity.mUmengEntry);
//            intent.putExtra("umeng_timestamp", mActivity.mUmengTimestamp);
//            if ("study_record".equals(mActivity.mFrom)) {
//                intent.putExtra("umeng_entry_review", "Record");
//            } else if ("mokao_homepage".equals(mActivity.mFrom)
//                    || "mokao_history_list".equals(mActivity.mFrom)) {
//                intent.putExtra("umeng_entry_review", "MokaoList");
//            } else {
//                intent.putExtra("umeng_entry_review", "Report");
//            }

            startActivity(intent);
            finish();
        }
    }

    public void showSJYS(String time) {
        mTvSJYS.setText(time);
    }
}
