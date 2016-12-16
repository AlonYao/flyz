package com.appublisher.quizbank.common.interview.fragment;

import android.os.Bundle;

/**
 * Created by huaxiao on 2016/12/16.
 */

public class InterviewUnPurchasedFragment extends InterviewDetailBaseFragment {

    public static InterviewUnPurchasedFragment newInstance() {
        Bundle args = new Bundle();
        InterviewUnPurchasedFragment fragment = new InterviewUnPurchasedFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
