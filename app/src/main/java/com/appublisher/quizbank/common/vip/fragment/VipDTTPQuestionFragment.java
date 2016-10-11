package com.appublisher.quizbank.common.vip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;

/**
 * 小班：单题突破 问题Fragment
 */

public class VipDTTPQuestionFragment extends Fragment{

    private static final String ARGS_DATA = "data";

    public static VipDTTPQuestionFragment newInstance(VipDTTPResp resp) {
        Bundle args = new Bundle();
        args.putString(ARGS_DATA, GsonManager.modelToString(resp));
        VipDTTPQuestionFragment fragment = new VipDTTPQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
