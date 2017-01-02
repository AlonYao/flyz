package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.model.MeasureSearchModel;

public class MeasureSearchActivity extends BaseActivity implements View.OnClickListener{

    private EditText mEtSearch;
    private MeasureSearchModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_search);
        setToolBar(this);
        initView();
        initData();
    }

    private void initData() {
        mModel = new MeasureSearchModel(this);
    }

    private void initView() {
        ImageButton btnSearch = (ImageButton) findViewById(R.id.measure_search_btn);
        if (btnSearch != null) {
            btnSearch.setVisibility(View.VISIBLE);
            btnSearch.setOnClickListener(this);
        }

        mEtSearch = (EditText) findViewById(R.id.measure_search_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measure_search_btn:
                if (mEtSearch == null) return;
                String text = mEtSearch.getText().toString();
                if (text.length() == 0) return;

                showLoading();
                mModel.search(text);
                break;
        }
    }
}
