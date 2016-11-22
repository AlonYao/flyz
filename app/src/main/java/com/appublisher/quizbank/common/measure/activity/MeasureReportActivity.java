package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.view.ViewStub;
import android.widget.TextView;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.MeasureReportModel;

public class MeasureReportActivity extends BaseActivity implements MeasureConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_report);
        initView();
        initData();
    }

    private void initData() {
        MeasureReportModel model = new MeasureReportModel(this);
        model.mPaperId = getIntent().getIntExtra(PAPER_ID, 0);
        model.mPaperType = getIntent().getStringExtra(PAPER_TYPE);
        model.getData();
    }

    private void initView() {

    }

    public void showPaperInfo(String type, String name) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_paperinfo_vs);
        if (vs == null) return;
        vs.inflate();

        TextView tvType = (TextView) findViewById(R.id.measure_report_type);
        TextView tvName = (TextView) findViewById(R.id.measure_report_name);

        if (tvType != null) {
            tvType.setText(type);
        }

        if (tvName != null) {
            tvName.setText(name);
        }
    }

    public void showRightAll(int right, int total) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_rightall_vs);
        if (vs == null) return;
        vs.inflate();

        TextView tvRight = (TextView) findViewById(R.id.measure_report_rightnum);
        TextView tvTotal = (TextView) findViewById(R.id.measure_report_totalnum);

        if (tvRight != null) {
            tvRight.setText(String.valueOf(right));
        }

        if (tvTotal != null) {
            tvTotal.setText(String.valueOf(total));
        }
    }

    public void showCategory() {
        
    }

    public void showNotes() {

    }
}
