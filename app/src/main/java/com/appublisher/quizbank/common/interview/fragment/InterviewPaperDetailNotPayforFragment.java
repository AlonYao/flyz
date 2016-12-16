package com.appublisher.quizbank.common.interview.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appublisher.quizbank.R;

/**
 * Created by Admin on 2016/12/16.
 * 此类是未付费的Fragment类
 */

public class InterviewPaperDetailNotPayforFragment extends Fragment{

    private View mMainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1.判断空的状态

        //2.生成对应的布局
        // view初始化
        mMainView = inflater.inflate(R.layout.fragment_interviewpaperdetailnotpayfor,container,false);


        return mMainView;
    }
}
