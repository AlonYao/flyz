package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;

/**
 * 做题模块
 */

public class MeasureItemFragment extends Fragment{

    private static final String ARGS_QUESTION = "question";
    private static final String ARGS_POSITION = "position";
    private static final String ARGS_SIZE = "size";

    private MeasureQuestion mQuestion;
    private LinearLayout mStemContainer;
    private LinearLayout mOptionAContainer;
    private LinearLayout mOptionBContainer;
    private LinearLayout mOptionCContainer;
    private LinearLayout mOptionDContainer;
    private int mPosition;
    private int mSize;

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
    }
}
