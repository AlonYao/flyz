package com.appublisher.quizbank.common.vip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.common.vip.netdata.VipDTTPResp;
import com.appublisher.quizbank.common.vip.netdata.VipMSJPResp;

/**
 * 小班：单题突破 材料Fragment
 */

public class VipDTTPMaterialFragment extends Fragment{

    private static final String ARGS_DATA = "data";
    private VipMSJPResp mResp;

    public static VipDTTPMaterialFragment newInstance(VipDTTPResp resp) {
        Bundle args = new Bundle();
        args.putString("data", GsonManager.modelToString(resp));
        VipDTTPMaterialFragment fragment = new VipDTTPMaterialFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
