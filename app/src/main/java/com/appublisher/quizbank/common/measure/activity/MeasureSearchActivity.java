package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.adapter.MeasureSearchAdapter;
import com.appublisher.quizbank.common.measure.model.MeasureSearchModel;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;

import java.util.List;

public class MeasureSearchActivity extends BaseActivity implements
        View.OnClickListener, XListView.IXListViewListener{

    private EditText mEtSearch;
    private MeasureSearchModel mModel;
    private XListView mListView;
    private MeasureSearchAdapter mAdapter;

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

        mListView = (XListView) findViewById(R.id.measure_search_lv);
        if (mListView != null) {
            mListView.setXListViewListener(this);
            mListView.setPullRefreshEnable(false);
            mListView.setPullLoadEnable(true);
        }
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

    @Override
    public void onRefresh() {
        // Empty
    }

    @Override
    public void onLoadMore() {
        mModel.loadMore();
    }

    public void stopXListView() {
        mListView.stopLoadMore();
        mListView.stopRefresh();
    }

    public void showContent(List<MeasureSearchResp.SearchItemBean> list) {
        mAdapter = new MeasureSearchAdapter(this, list);
        mListView.setAdapter(mAdapter);
    }
}
