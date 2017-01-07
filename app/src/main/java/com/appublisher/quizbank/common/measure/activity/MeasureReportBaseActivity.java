package com.appublisher.quizbank.common.measure.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.Utils;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.bean.MeasureNotesBean;
import com.appublisher.quizbank.common.measure.bean.MeasureReportCategoryBean;
import com.appublisher.quizbank.common.measure.view.IMeasureReportBaseView;

import java.util.List;

public class MeasureReportBaseActivity extends BaseActivity implements IMeasureReportBaseView{

    public static final String FROM_MOCK_REPORT = "from_mock_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showCategory(List<MeasureReportCategoryBean> list, String from) {
        if (list == null || list.size() == 0) return;

        ViewStub vs = (ViewStub) findViewById(R.id.measure_report_category_vs);
        if (vs == null) return;
        vs.inflate();
        LinearLayout container =
                (LinearLayout) findViewById(R.id.measure_report_category_container);
        if (container == null) return;

        for (MeasureReportCategoryBean categoryBean : list) {
            if (categoryBean == null) continue;

            View child;
            if (FROM_MOCK_REPORT.equals(from)) {
                child = LayoutInflater.from(this).inflate(
                        R.layout.practice_report_category_formock, container, false);
            } else {
                child = LayoutInflater.from(this).inflate(
                        R.layout.practice_report_category, container, false);
            }

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
                tvCategoryTime.setText(String.valueOf(
                        Utils.getSpeedByRound(categoryDuration, categoryTotalNum)));
            }

            container.addView(child);
        }
    }

    @Override
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
}
