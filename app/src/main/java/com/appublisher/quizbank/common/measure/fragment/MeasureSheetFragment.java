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

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.measure.bean.MeasureQuestionBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 做题模块：答题纸
 */

public class MeasureSheetFragment extends DialogFragment {

    private static final String ARGS_QUESTIONS = "questions";

    private List<MeasureQuestionBean> mQuestions;

    public static MeasureSheetFragment newInstance(String questions) {
        Bundle args = new Bundle();
        args.putString(ARGS_QUESTIONS, questions);
        MeasureSheetFragment fragment = new MeasureSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全屏处理
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Measure_Dialog_FullScreen);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.measure_sheet_fragment, container);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.themecolor));
        toolbar.setNavigationIcon(R.drawable.scratch_paper_exit);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
        mQuestions = new ArrayList<>();
        String questions = getArguments().getString(ARGS_QUESTIONS);
        try {
            JSONArray array = new JSONArray(questions);
            int length = array.length();
            for (int i = 0; i < length; i++) {
                JSONObject object = array.getJSONObject(i);
                mQuestions.add(GsonManager.getModel(object, MeasureQuestionBean.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showContent() {
        if (isEntirePaper()) {
            showEntire();
        } else {
            showCommon();
        }
//        if (mQuestions == null) return;
//        int size = mQuestions.size();
//        for (int i = 0; i < size; i++) {
//            MeasureQuestionBean questionBean = mQuestions.get(i);
//            if (questionBean == null) continue;
//
//        }
    }

    private void showEntire() {

    }

    private void showCommon() {

    }

    private boolean isEntirePaper() {
        // 判断依据：是否有说明页
        if (mQuestions == null || mQuestions.size() == 0) return false;
        MeasureQuestionBean questionBean = mQuestions.get(0);
        return questionBean != null && questionBean.is_desc();
    }
}
