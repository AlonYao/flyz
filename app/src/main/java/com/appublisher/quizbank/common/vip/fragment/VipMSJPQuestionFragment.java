package com.appublisher.quizbank.common.vip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 小班：名师精批 问题Tab
 */
public class VipMSJPQuestionFragment extends Fragment {

    public static VipMSJPQuestionFragment newInstance() {
        Bundle args = new Bundle();
        VipMSJPQuestionFragment fragment = new VipMSJPQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(getContext());
        textView.setText("问题");
        return textView;
    }

}
