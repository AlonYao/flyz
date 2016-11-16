package com.appublisher.quizbank.common.interview.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.interview.activity.InterviewGuoKaoActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewCategoryActivity;
import com.appublisher.quizbank.common.interview.activity.InterviewPaperListActivity;

/**
 * Created by jinbao on 2016/11/14.
 */

public class InterviewIndexFragment extends Fragment {

    private View guokaoView;
    private View starAnalysisView;
    private View categoryView;
    private View historyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_interview_index, null);

        guokaoView = view.findViewById(R.id.guokao_view);
        starAnalysisView = view.findViewById(R.id.star_analysis_view);
        categoryView = view.findViewById(R.id.category_view);
        historyView = view.findViewById(R.id.history_view);

        setValue();

        return view;
    }

    public void setValue() {
        guokaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewGuoKaoActivity.class);
                startActivity(intent);
            }
        });

        starAnalysisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "teacher");
                startActivity(intent);
            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewCategoryActivity.class);
                startActivity(intent);
            }
        });

        historyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), InterviewPaperListActivity.class);
                intent.putExtra("from", "history");
                startActivity(intent);
            }
        });
    }
}
