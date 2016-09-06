package com.appublisher.quizbank.common.vip.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.QuizBankApp;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;

import org.json.JSONObject;

/**
 * Created by jinbao on 2016/9/2.
 */
public class VipExerciseIndexModel {

    private static PopupWindow statusPop;
    private static PopupWindow categoryPop;
    private static PopupWindow typePop;


    public static void dealExerciseFilter(JSONObject response) {
        VipExerciseFilterResp vipExerciseFilterResp = GsonManager.getModel(response, VipExerciseFilterResp.class);
        View statusView = LayoutInflater.from(QuizBankApp.getInstance().getApplicationContext()).inflate(R.layout.vip_pop_status, null);
        GridView gridView = (GridView) statusView.findViewById(R.id.gridview);

        statusPop = new PopupWindow(statusView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        statusPop.setOutsideTouchable(true);
    }

}
