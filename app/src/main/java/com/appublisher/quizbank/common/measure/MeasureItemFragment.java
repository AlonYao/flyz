package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;

/**
 * 做题模块
 */

public class MeasureItemFragment extends Fragment implements View.OnClickListener{

    private static final String ARGS_QUESTION = "question";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_SIZE = "size";

    private MeasureQuestion mQuestion;
    private LinearLayout mStemContainer;
    private LinearLayout mOptionAContainer;
    private LinearLayout mOptionBContainer;
    private LinearLayout mOptionCContainer;
    private LinearLayout mOptionDContainer;
    private TextView mTvOptionA;
    private TextView mTvOptionB;
    private TextView mTvOptionC;
    private TextView mTvOptionD;
    private int mPosition;
    private int mSize;
    private boolean mOptionClick;

    public static MeasureItemFragment newInstance(String question, int position, int size) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION, question);
        args.putInt(ARGS_POSITION, position);
        args.putInt(ARGS_SIZE, size);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuestion = GsonManager.getModel(
                getArguments().getString(ARGS_QUESTION), MeasureQuestion.class);
        mPosition = getArguments().getInt(ARGS_POSITION);
        mSize = getArguments().getInt(ARGS_SIZE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.measure_item_withoutmaterial, container, false);
        mStemContainer = (LinearLayout) root.findViewById(R.id.measure_stem_container);
        mOptionAContainer = (LinearLayout) root.findViewById(
                R.id.measure_option_a_container);
        mOptionBContainer = (LinearLayout) root.findViewById(
                R.id.measure_option_b_container);
        mOptionCContainer = (LinearLayout) root.findViewById(
                R.id.measure_option_c_container);
        mOptionDContainer = (LinearLayout) root.findViewById(
                R.id.measure_option_d_container);
        mTvOptionA = (TextView) root.findViewById(R.id.measure_option_a_tv);
        mTvOptionB = (TextView) root.findViewById(R.id.measure_option_b_tv);
        mTvOptionC = (TextView) root.findViewById(R.id.measure_option_c_tv);
        mTvOptionD = (TextView) root.findViewById(R.id.measure_option_d_tv);
        showContent();
        return root;
    }

    private void showContent() {
        if (mQuestion == null) return;
        // 处理题号
        String stem = String.valueOf(mPosition + 1) + "/" + String.valueOf(mSize) + "  ";
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

        mOptionAContainer.setOnClickListener(this);
        mOptionBContainer.setOnClickListener(this);
        mOptionCContainer.setOnClickListener(this);
        mOptionDContainer.setOnClickListener(this);

        mTvOptionA.setOnClickListener(this);
        mTvOptionB.setOnClickListener(this);
        mTvOptionC.setOnClickListener(this);
        mTvOptionD.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.measure_option_a_tv:
            case R.id.measure_option_b_tv:
            case R.id.measure_option_c_tv:
            case R.id.measure_option_d_tv:
                optionOnClickAction((TextView) v);
                break;

            case R.id.measure_option_a_container:
                optionOnClickAction(mTvOptionA);
                break;

            case R.id.measure_option_b_container:
                optionOnClickAction(mTvOptionB);
                break;

            case R.id.measure_option_c_container:
                optionOnClickAction(mTvOptionC);
                break;

            case R.id.measure_option_d_container:
                optionOnClickAction(mTvOptionD);
                break;
        }
    }

    /**
     * 重置按钮状态
     */
    private void resetOption() {
        mTvOptionA.setSelected(false);
        mTvOptionB.setSelected(false);
        mTvOptionC.setSelected(false);
        mTvOptionD.setSelected(false);
    }

    /**
     * 选项点击动作
     * @param textView 选项
     */
    public void optionOnClickAction(final TextView textView) {
        if (mOptionClick) return;

        mOptionClick = true;
        resetOption();
        textView.setSelected(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageSkip();
                mOptionClick = false;
            }
        }, 100);
    }

    /**
     * 页面跳转
     */
    private void pageSkip() {
        if (mPosition + 1 == mSize) {
            Toast.makeText(getContext(), "答题卡", Toast.LENGTH_SHORT).show();
        } else {
            if (getActivity() instanceof MeasureActivity) {
                ((MeasureActivity) getActivity()).mViewPager.setCurrentItem(mPosition + 1);
            }
        }
    }
}
