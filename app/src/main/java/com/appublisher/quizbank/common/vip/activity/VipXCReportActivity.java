package com.appublisher.quizbank.common.vip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.vip.model.VipXCReportModel;

import java.util.HashMap;

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
            // 全部
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(
                    MeasureConstants.INTENT_ANALYSIS_BEAN,
                    GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.VIP);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "All");
            UmengManager.onEvent(this, "Report", map);

        } else if (v.getId() == R.id.vip_xc_report_error) {
            // 错题
            if (mModel.isAllRight()) return;
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(
                    MeasureConstants.INTENT_ANALYSIS_BEAN,
                    GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(MeasureConstants.INTENT_ANALYSIS_IS_ERROR_ONLY, true);
            intent.putExtra(MeasureConstants.INTENT_PAPER_TYPE, MeasureConstants.VIP);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Error");
            UmengManager.onEvent(this, "Report", map);
        }
    }

    public void showSJYS(String time) {
        mTvSJYS.setText(time);
    }
}
