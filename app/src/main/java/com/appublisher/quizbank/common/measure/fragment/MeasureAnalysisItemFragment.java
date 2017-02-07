package com.appublisher.quizbank.common.measure.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.activity.MeasureAnalysisActivity;
import com.appublisher.quizbank.common.measure.bean.MeasureAnswerBean;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;
import com.appublisher.quizbank.common.measure.model.MeasureAnalysisModel;
import com.appublisher.quizbank.common.measure.model.MeasureModel;

import java.math.BigDecimal;

/**
 * 做题模块：解析页面
 */

public class MeasureAnalysisItemFragment extends MeasureBaseFragment {

    private static final String ARGS_QUESTION = "question";
    private static final String ARGS_ANSWER = "answer";
    private static final String ARGS_POSITION = "position";


    private MeasureAnswerBean mAnswer;
    private LinearLayout mStemContainer; // 题干
    private LinearLayout mOptionAContainer;
    private LinearLayout mOptionBContainer;
    private LinearLayout mOptionCContainer;
    private LinearLayout mOptionDContainer;
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;
    private int mPosition;

    public static MeasureAnalysisItemFragment newInstance(String question,
                                                          String answer,
                                                          int position) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION, question);
        args.putString(ARGS_ANSWER, answer);
        args.putInt(ARGS_POSITION, position);
        MeasureAnalysisItemFragment fragment = new MeasureAnalysisItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestion = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTION), MeasureQuestionBean.class);
        mAnswer = GsonManager.getModel(
                getArguments().getString(ARGS_ANSWER), MeasureAnswerBean.class);
        mPosition = getArguments().getInt(ARGS_POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.measure_item, container, false);
        initView();
        showContent();
        return mRoot;
    }

    private void showContent() {
        if (mQuestion == null) return;
        // 处理题号
        String stem =
                String.valueOf(mQuestion.getQuestion_order())
                        + "/" + String.valueOf(mQuestion.getQuestion_amount()) + "  ";
        stem = stem + mQuestion.getQuestion();
        MeasureModel.addRichTextToContainer(getContext(), mStemContainer, stem, true);
        // 选项
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionAContainer, mQuestion.getOption_a(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionBContainer, mQuestion.getOption_b(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionCContainer, mQuestion.getOption_c(), false);
        MeasureModel.addRichTextToContainer(
                getContext(), mOptionDContainer, mQuestion.getOption_d(), false);
        // 选项状态
        setOptionStatus();
        // 材料
        showMaterial(mQuestion.getMaterial());
        // 解析部分
        showAnalysis();
    }

    private void showNoAnswerFlag() {
        ViewStub vs = (ViewStub) mRoot.findViewById(R.id.measure_noanswer_viewstub);
        vs.inflate();
    }

    private void showAnalysis() {
        if (mQuestion == null) return;

        ViewStub vs = (ViewStub) mRoot.findViewById(R.id.measure_analysis_viewstub);
        vs.inflate();

        TextView tvRightAnswer = (TextView) mRoot.findViewById(R.id.measure_analysis_rightanswer);
        TextView tvNote = (TextView) mRoot.findViewById(R.id.measure_analysis_note);
        TextView tvSource = (TextView) mRoot.findViewById(R.id.measure_analysis_source);
        TextView tvCategory = (TextView) mRoot.findViewById(R.id.measure_analysis_accuracy);
        LinearLayout llMeasureAnalysis =
                (LinearLayout) mRoot.findViewById(R.id.measure_analysis_container);

        // 正确答案
        String rightAnswer = mQuestion.getAnswer();
        String sRight = "【正确答案】 " + (rightAnswer == null ? "" : rightAnswer) + "；";
        if (mAnswer != null && mAnswer.getAnswer() != null && mAnswer.getAnswer().length() > 0) {
            sRight = sRight + "你的选择是" + mAnswer.getAnswer();
        } else {
            // 显示未答
            if (!isFromSearch()) {
                showNoAnswerFlag();
            }
        }
        tvRightAnswer.setText(sRight);

        // 统计
        showSummary(tvCategory);

        // 解析
        MeasureModel.addRichTextToContainer(
                getContext(), llMeasureAnalysis, "【解析】 " + mQuestion.getAnalysis(), true);

        // 知识点
        String note = "【知识点】 " + mQuestion.getNote_name();
        tvNote.setText(note);

        // 来源
        String source = "【来源】 " + mQuestion.getSource();
        tvSource.setText(source);
    }

    /**
     * 显示统计信息
     * @param textView TextView
     */
    private void showSummary(TextView textView) {
        if (mQuestion == null) return;
        double summaryAccuracy = mQuestion.getSummary_accuracy();
        int summary_count = mQuestion.getSummary_count();
        String summary_fallible = mQuestion.getSummary_fallible();
        String data = "";

        if (summary_count != 0) {
            data = data + "全站作答" + String.valueOf(summary_count) + "次";
        }

        if (summaryAccuracy != 0) {
            if (data.length() != 0) data = data + "，";
            summaryAccuracy = summaryAccuracy*100;
            BigDecimal bigDecimal = new BigDecimal(summaryAccuracy);
            summaryAccuracy = bigDecimal.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
            data = data + "正确率" + summaryAccuracy + "%";
        }

        if (summary_fallible != null && summary_fallible.length() != 0) {
            if (data.length() != 0) data = data + "，";
            data = data + "易错项为" + summary_fallible;
        }

        if (data.length() == 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.VISIBLE);
            data = "【统计】 " + data;
            textView.setText(data);
        }
    }

    private void setOptionStatus() {
        if (mQuestion == null) return;

        // 正确答案
        String rightAnswer = mQuestion.getAnswer();
        if (OPTION_A.equals(rightAnswer)) {
            setOptionBg(mTvOptionA, true);
        } else if (OPTION_B.equals(rightAnswer)) {
            setOptionBg(mTvOptionB, true);
        } else if (OPTION_C.equals(rightAnswer)) {
            setOptionBg(mTvOptionC, true);
        } else if (OPTION_D.equals(rightAnswer)) {
            setOptionBg(mTvOptionD, true);
        }

        // 错误答案
        if (mAnswer == null || mAnswer.is_right()) return;
        String userAnswer = mAnswer.getAnswer();
        if (OPTION_A.equals(userAnswer)) {
            setOptionBg(mTvOptionA, false);
        } else if (OPTION_B.equals(userAnswer)) {
            setOptionBg(mTvOptionB, false);
        } else if (OPTION_C.equals(userAnswer)) {
            setOptionBg(mTvOptionC, false);
        } else if (OPTION_D.equals(userAnswer)) {
            setOptionBg(mTvOptionD, false);
        }
    }

    private void setOptionBg(TextView textView, boolean isRight) {
        int resId = R.drawable.measure_analysis_wrong;
        if (isRight) {
            resId = R.drawable.measure_analysis_right;
        }
        textView.setBackgroundResource(resId);
        textView.setTextColor(Color.WHITE);
    }

    private void initView() {
        mStemContainer = (LinearLayout) mRoot.findViewById(R.id.measure_stem_container);
        mOptionAContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_a_container);
        mOptionBContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_b_container);
        mOptionCContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_c_container);
        mOptionDContainer = (LinearLayout) mRoot.findViewById(
                R.id.measure_option_d_container);
        mTvOptionA = (TextView) mRoot.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) mRoot.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) mRoot.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) mRoot.findViewById(R.id.measure_option_d_tv);
    }

    private boolean isFromSearch() {
        if (getActivity() instanceof MeasureAnalysisActivity) {
            MeasureAnalysisModel model = ((MeasureAnalysisActivity) getActivity()).mModel;
            return model.mIsFromSearch;
        }
        return false;
    }
}
