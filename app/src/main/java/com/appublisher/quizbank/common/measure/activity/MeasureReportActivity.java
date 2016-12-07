package com.appublisher.quizbank.common.measure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.MeasureConstants;
import com.appublisher.quizbank.common.measure.bean.MeasureNotesBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.bean.MeasureScoresBean;
import com.appublisher.quizbank.common.measure.model.MeasureReportModel;

import java.util.HashMap;
import java.util.List;

public class MeasureReportActivity extends BaseActivity implements
        MeasureConstants, View.OnClickListener {

    private MeasureReportModel mModel;

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

    private void initData() {
        mModel = new MeasureReportModel(this);
        mModel.mPaperId = getIntent().getIntExtra(INTENT_PAPER_ID, 0);
        mModel.mPaperType = getIntent().getStringExtra(INTENT_PAPER_TYPE);

        showLoading();
        mModel.getData();
    }

    private void initView() {
        Button btnAll = (Button) findViewById(R.id.measure_report_all);
        Button btnError = (Button) findViewById(R.id.measure_report_error);

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

    public void showCategory(List<MeasureReportCategoryBean> list) {
        if (list == null || list.size() == 0) return;

        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_category_vs);
        if (vs == null) return;
        vs.inflate();
        LinearLayout container =
                (LinearLayout) findViewById(R.id.measure_report_category_container);
        if (container == null) return;

        for (MeasureReportCategoryBean categoryBean : list) {
            if (categoryBean == null) continue;

            View child = LayoutInflater.from(this).inflate(
                    R.layout.practice_report_category, container, false);
            TextView tvCategoryName =
                    (TextView) child.findViewById(R.id.practice_report_category);
            TextView tvCategoryRightNum =
                    (TextView) child.findViewById(R.id.practice_report_category_rightnum);
            TextView tvCategoryTotalNum =
                    (TextView) child.findViewById(R.id.practice_report_category_totalnum);
            TextView tvCategoryTime =
                    (TextView) child.findViewById(R.id.practice_report_category_time);
            ImageView ivCategoryPb =
                    (ImageView) child.findViewById(R.id.practice_report_category_pb);

            tvCategoryName.setText(categoryBean.getCategory_name());

            int categoryRightNum = categoryBean.getRightNum();
            int categoryTotalNum = categoryBean.getTotalNum();
            int categoryDuration = categoryBean.getDuration();

            tvCategoryRightNum.setText(String.valueOf(categoryRightNum));
            tvCategoryTotalNum.setText(String.valueOf(categoryTotalNum));

            // 进度条
            if (categoryTotalNum != 0) {
                int pbLength = (categoryRightNum * 146) / categoryTotalNum;
                if (pbLength == 0) pbLength++;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.width = pbLength * 2;
                lp.height = 10;
                ivCategoryPb.setLayoutParams(lp);
                int ratio = categoryDuration / categoryTotalNum;
                tvCategoryTime.setText(String.valueOf(ratio));
            }

            container.addView(child);
        }
    }

    public void showNotes(List<MeasureNotesBean> list) {
        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_notes_vs);
        if (vs == null) return;
        vs.inflate();

        ImageView ivNoChange = (ImageView) findViewById(R.id.measure_report_notenochange);
        LinearLayout container = (LinearLayout) findViewById(R.id.measure_report_notes_container);
        if (ivNoChange == null || container == null) return;

        if (list == null || list.size() == 0) {
            ivNoChange.setVisibility(View.VISIBLE);
        } else {
            ivNoChange.setVisibility(View.GONE);

            for (MeasureNotesBean notesBean : list) {
                if (notesBean == null) continue;

                View child = LayoutInflater.from(this).inflate(
                        R.layout.practice_report_note, container, false);
                TextView tvNoteName = (TextView) child.findViewById(R.id.practice_report_note);
                ImageView ivLevelPre =
                        (ImageView) child.findViewById(R.id.practice_report_note_pre);
                ImageView ivLevelNow =
                        (ImageView) child.findViewById(R.id.practice_report_note_now);
                ImageView ivLevelChange =
                        (ImageView) child.findViewById(R.id.practice_report_note_change);

                tvNoteName.setText(notesBean.getName());

                int levelPre = notesBean.getFrom();
                int levelNow = notesBean.getTo();

                setLevelImg(levelPre, ivLevelPre);
                setLevelImg(levelNow, ivLevelNow);

                if (levelPre > levelNow) {
                    ivLevelChange.setImageResource(R.drawable.practice_report_down);
                } else {
                    ivLevelChange.setImageResource(R.drawable.practice_report_up);
                }

                container.addView(child);
            }
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

    /**
     * 设置知识点变化img
     *
     * @param level 知识点等级
     * @param view  知识点等级view
     */
    private void setLevelImg(int level, ImageView view) {
        if (level == 0) {
            view.setImageResource(R.drawable.practice_report_level0);
        } else if (level == 1) {
            view.setImageResource(R.drawable.practice_report_level1);
        } else if (level == 2) {
            view.setImageResource(R.drawable.practice_report_level2);
        } else if (level == 3) {
            view.setImageResource(R.drawable.practice_report_level3);
        } else if (level == 4) {
            view.setImageResource(R.drawable.practice_report_level4);
        } else if (level == 5) {
            view.setImageResource(R.drawable.practice_report_level5);
        } else {
            view.setImageResource(R.drawable.practice_report_level0);
        }
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
