package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.appublisher.quizbank.R;

/**
 * Created by Admin on 2017/1/9.
 */

public class InterviewGuideFragment extends DialogFragment {

    private Button mButton;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏处理
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Measure_Dialog_FullScreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.interview_guide_background, container);
        initView();
        initListener();
        return view;
    }

    private void initView() {
        mButton = (Button) view.findViewById(R.id.interview_guide_button);
    }
    private void initListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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
}
