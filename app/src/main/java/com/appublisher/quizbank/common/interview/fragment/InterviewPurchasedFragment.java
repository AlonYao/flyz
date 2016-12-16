package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewPurchasedFragment extends InterviewDetailBaseFragment {

    public static InterviewPurchasedFragment newInstance() {
        Bundle args = new Bundle();
        InterviewPurchasedFragment fragment = new InterviewPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
