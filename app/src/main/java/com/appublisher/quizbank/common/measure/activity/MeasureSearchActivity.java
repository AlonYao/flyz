package com.appublisher.quizbank.common.measure.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.adapter.MeasureSearchAdapter;
import com.appublisher.quizbank.common.measure.model.MeasureSearchModel;
import com.appublisher.quizbank.common.measure.netdata.MeasureSearchResp;

import java.util.List;

public class MeasureSearchActivity extends BaseActivity implements
        View.OnClickListener, XListView.IXListViewListener{

    public MeasureSearchModel mModel;

    private EditText mEtSearch;
    private XListView mListView;
    private MeasureSearchAdapter mAdapter;
    private LinearLayout mNoticeView;
    private TextView mNoneView;
    private TextView mTvKeywords;
    private TextView mTvCount;

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

        mNoticeView = (LinearLayout) findViewById(R.id.measure_search_notice);
        mNoneView = (TextView) findViewById(R.id.measure_search_none);
        mTvKeywords = (TextView) findViewById(R.id.measure_search_keywords);
        mTvCount = (TextView) findViewById(R.id.measure_search_count);
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

                hideSoftKeyboard();
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

    public void showLoadMore(List<MeasureSearchResp.SearchItemBean> list) {
        if (mAdapter == null) showContent(list);
        mAdapter.notifyDataSetChanged();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() == null) return;
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void showNoMoreToast() {
        Toast.makeText(this, "暂无更多内容", Toast.LENGTH_SHORT).show();
    }

    public void showNotice(String keywords, int count) {
        mNoticeView.setVisibility(View.VISIBLE);
        mNoneView.setVisibility(View.GONE);

        mTvKeywords.setTextColor(Color.RED);
        if (keywords != null && keywords.length() > 10) {
            keywords = keywords.substring(0, 10);
            keywords = keywords + "…";
        }
        keywords = "\"" + keywords + "\"";
        mTvKeywords.setText(keywords);

        String countString = "行测题目，共有" + String.valueOf(count) + "题";
        mTvCount.setText(countString);
    }

    public void showNone() {
        mNoticeView.setVisibility(View.GONE);
        mNoneView.setVisibility(View.VISIBLE);
    }
}
