package com.appublisher.quizbank.common.measure.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 做题页面
 */

public class MeasureTabAllRightFragment extends Fragment{

    public static MeasureTabAllRightFragment newInstance() {
        Bundle args = new Bundle();
        MeasureTabAllRightFragment fragment = new MeasureTabAllRightFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
