package com.appublisher.quizbank.common.measure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureScoresBean;
import com.appublisher.quizbank.common.measure.model.MeasureReportModel;
import com.appublisher.quizbank.model.business.CommonModel;

import java.util.HashMap;
import java.util.List;

public class MeasureReportActivity extends MeasureReportBaseActivity implements
        MeasureConstants, View.OnClickListener {

    private static final String MENU_SHARE = "分享";

    private MeasureReportModel mModel;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_report);
        setToolBar(this);
        setTitle(R.string.measure_report);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Umeng
        HashMap<String, String> map = new HashMap<>();
        map.put("Action", "Back");
        UmengManager.onEvent(this, "Report", map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(
                menu.add(MENU_SHARE).setIcon(R.drawable.share),
                MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            CommonModel.checkUmengShare(this, new CommonModel.ShareCheckListener() {
                @Override
                public void onShare() {
                    mModel.setUmengShare();
                }
            });
        } else if ("分享".equals(item.getTitle())) {
            mModel.setUmengShare();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        CommonModel.checkUmengShare(this, new CommonModel.ShareCheckListener() {
            @Override
            public void onShare() {
                mModel.setUmengShare();
            }
        });
    }

    private void initData() {
        mModel = new MeasureReportModel(this);
        mModel.mPaperId = getIntent().getIntExtra(INTENT_PAPER_ID, 0);
        mModel.mPaperType = getIntent().getStringExtra(INTENT_PAPER_TYPE);
        mModel.setScrollView(mScrollView);

        showLoading();
        mModel.getData();
    }

    private void initView() {
        Button btnAll = (Button) findViewById(R.id.measure_report_all);
        Button btnError = (Button) findViewById(R.id.measure_report_error);
        mScrollView = (ScrollView) findViewById(R.id.measure_report_sv);

        if (btnAll != null) {
            btnAll.setOnClickListener(this);
        }

        if (btnError != null) {
            btnError.setOnClickListener(this);
        }
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

    public void showYourScore(String text) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_yourscore_vs);
        if (vs == null) return;
        vs.inflate();

        TextView tvScore = (TextView) findViewById(R.id.measure_report_yourscore_tv);
        if (tvScore != null) {
            tvScore.setText(text);
        }
    }

    public void showStatistics(String rank, String score) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_statistics_vs);
        if (vs == null) return;
        vs.inflate();

        TextView tvRank = (TextView) findViewById(R.id.measure_report_statistics_rank);
        TextView tvScore = (TextView) findViewById(R.id.measure_report_statistics_score);

        if (tvRank != null) {
            tvRank.setText(rank);
        }

        if (tvScore != null) {
            tvScore.setText(score);
        }
    }

    public void showStandings(Spannable text) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_standings_vs);
        if (vs == null) return;
        vs.inflate();

        TextView tvStandings = (TextView) findViewById(R.id.measure_report_standings);

        if (tvStandings != null) {
            tvStandings.setText(text);
        }
    }

    public void showBorderline(List<MeasureScoresBean> scores) {
        if (scores == null) return;

        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_borderline_vs);
        if (vs == null) return;
        vs.inflate();

        LinearLayout container =
                (LinearLayout) findViewById(R.id.measure_report_borderline_container);
        if (container == null) return;

        int size = scores.size();
        for (int i = 0; i < size; i++) {
            MeasureScoresBean score = scores.get(i);
            if (score == null) continue;

            View child = LayoutInflater.from(this)
                    .inflate(R.layout.practice_report_borderline_item, container, false);

            TextView tvName =
                    (TextView) child.findViewById(R.id.item_borderline_name);
            TextView tvNum =
                    (TextView) child.findViewById(R.id.item_borderline_num);
            View line = child.findViewById(R.id.item_borderline_line);

            tvName.setText(score.getName());
            tvNum.setText(String.valueOf(score.getScore()));

            if (i == size - 1) {
                line.setVisibility(View.GONE);
            } else {
                line.setVisibility(View.VISIBLE);
            }

            container.addView(child);
        }

        // 说明文字
        TextView textView = new TextView(this);
        textView.setText(R.string.practice_report_borderline_desc);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
                Utils.dip2px(this, 36),
                Utils.dip2px(this, 5),
                Utils.dip2px(this, 36),
                0);
        textView.setLayoutParams(layoutParams);
        container.addView(textView);
    }

    public void showCategory(List<MeasureReportCategoryBean> list) {
        showCategory(list, null);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.measure_report_all) {
            // 全部
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(INTENT_ANALYSIS_BEAN, GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(INTENT_PAPER_TYPE, mModel.mPaperType);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "All");
            UmengManager.onEvent(this, "Report", map);

        } else if (v.getId() == R.id.measure_report_error) {
            // 错题
            if (mModel.isAllRight()) return;
            Intent intent = new Intent(this, MeasureAnalysisActivity.class);
            intent.putExtra(INTENT_ANALYSIS_BEAN, GsonManager.modelToString(mModel.mAnalysisBean));
            intent.putExtra(INTENT_ANALYSIS_IS_ERROR_ONLY, true);
            intent.putExtra(INTENT_PAPER_TYPE, mModel.mPaperType);
            startActivity(intent);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Error");
            UmengManager.onEvent(this, "Report", map);
        }
    }
}
