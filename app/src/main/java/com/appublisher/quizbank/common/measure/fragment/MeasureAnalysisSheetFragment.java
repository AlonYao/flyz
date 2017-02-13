package com.appublisher.quizbank.common.measure.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.customui.ExpandableHeightGridView;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.adapter.MeasureAnalysisSheetAdapter;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.model.MeasureAnalysisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题页面
 */

public class MeasureAnalysisSheetFragment extends DialogFragment {

    private List<MeasureQuestionBean> mQuestions;
    private List<MeasureAnswerBean> mAnswers;
    private LinearLayout mContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Measure_Dialog_FullScreen);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.measure_analysis_sheet_fragment, container);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.themecolor));
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
        toolbar.setTitle(R.string.measure_sheet_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mContainer = (LinearLayout) view.findViewById(R.id.measure_sheet_container);

        showContent();

        return view;
    }

    @Override
    public void onResume() {
        // 全屏处理
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        super.onResume();
    }

    private void initData() {
        if (!(getActivity() instanceof MeasureAnalysisActivity)) return;
        MeasureAnalysisModel model = ((MeasureAnalysisActivity) getActivity()).mModel;
        if (model == null) return;

        mQuestions = model.getAdapterQuestions();
        mAnswers = model.getAdapterAnswers();
    }

    private void showContent() {
        if (isEntirePaper()) {
            showEntire();
        } else {
            showCommon();
        }
    }

    private boolean isEntirePaper() {
        // 判断依据：是否有说明页
        if (mQuestions == null || mQuestions.size() == 0) return false;
        MeasureQuestionBean questionBean = mQuestions.get(0);
        return questionBean != null && questionBean.is_desc();
    }

    private void showEntire() {
        if (mQuestions == null) return;

        List<MeasureQuestionBean> questionsForAdapter = new ArrayList<>();
        List<MeasureAnswerBean> answersForAdapter = new ArrayList<>();

        // 遍历
        int size = mQuestions.size();
        for (int i = 0; i < size; i++) {
            MeasureQuestionBean questionBean = mQuestions.get(i);
            if (questionBean == null) continue;
            if (questionBean.is_desc()) {
                if (questionsForAdapter.size() != 0) {
                    // 添加GridView
                    String name = questionsForAdapter.get(0).getCategory_name();
                    addGridViewItem(name, questionsForAdapter, answersForAdapter);

                    // 重置
                    questionsForAdapter = new ArrayList<>();
                    answersForAdapter = new ArrayList<>();
                }
            } else {
                // 添加题目
                questionsForAdapter.add(questionBean);

                // 添加用户答案
                if (mAnswers != null && i < mAnswers.size()) {
                    answersForAdapter.add(mAnswers.get(i));
                }

                if (i == size - 1) {
                    String name = questionsForAdapter.get(0).getCategory_name();
                    addGridViewItem(name, questionsForAdapter, answersForAdapter);
                }
            }
        }
    }

    private void showCommon() {
        addGridViewItem("", mQuestions, mAnswers);
    }

    private void addGridViewItem(String category,
                                 List<MeasureQuestionBean> questions,
                                 List<MeasureAnswerBean> answers) {
        if (questions == null) return;

        // init view
        View child = LayoutInflater.from(getActivity()).inflate(
                R.layout.measure_sheet_item, mContainer, false);
        TextView tvCategory = (TextView) child.findViewById(R.id.measure_sheet_item_tv);
        ExpandableHeightGridView gridView =
                (ExpandableHeightGridView)
                        child.findViewById(R.id.measure_sheet_item_gv);

        tvCategory.setText(category == null ? "" : category);

        MeasureAnalysisSheetAdapter adapter = new MeasureAnalysisSheetAdapter(
                this, questions, answers);
        gridView.setAdapter(adapter);

        mContainer.addView(child);
    }

    public void skip(int index) {
        if (!(getActivity() instanceof MeasureAnalysisActivity)) return;
        ((MeasureAnalysisActivity) getActivity()).mViewPager.setCurrentItem(index);
        dismiss();
    }
}
