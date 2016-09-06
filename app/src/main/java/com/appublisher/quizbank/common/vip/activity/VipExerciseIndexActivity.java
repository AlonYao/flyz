package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.Logger;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.customui.XListView;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.model.VipExerciseIndexModel;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class VipExerciseIndexActivity extends BaseActivity implements RequestCallback {

    public View statusView;
    public View categoryView;
    public View typeView;
    private TextView statusText;
    private TextView categoryText;
    private TextView typeText;
    private XListView listView;
    private VipRequest mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_exercise_index);

        setToolBar(this);
        mRequest = new VipRequest(this, this);

        initViews();
        setValues();
    }

    public void initViews() {
        statusView = findViewById(R.id.vip_status_rl);
        categoryView = findViewById(R.id.vip_category_rl);
        typeView = findViewById(R.id.vip_type_rl);
        statusText = (TextView) findViewById(R.id.vip_status_tv);
        categoryText = (TextView) findViewById(R.id.vip_category_tv);
        typeText = (TextView) findViewById(R.id.vip_type_tv);

        setValues();

        mRequest.getVipFilter();

    }

    public void setValues() {
        statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null) return;
        if ("vip_filter".equals(apiName)) {
            VipExerciseIndexModel.dealExerciseFilter(response);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {

    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {

    }
}
