package com.appublisher.quizbank.common.measure;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 做题模块
 */

public class MeasureItemFragment extends Fragment{

    private static final String ARGS_QUESTION = "question";

    public static MeasureItemFragment newInstance(String question) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTION, question);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
