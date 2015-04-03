package com.appublisher.quizbank.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.MeasureActivity;
import com.appublisher.quizbank.model.login.activity.LoginActivity;

/**
 * 首页
 */
public class HomePageFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // View 初始化
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        TextView textView = (TextView) view.findViewById(R.id.homepage_quicktest);

        // 快速练习
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, MeasureActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
