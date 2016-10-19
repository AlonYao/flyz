package com.appublisher.quizbank.common.vip.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.lib_basic.activity.BaseActivity;
import com.appublisher.lib_basic.volley.RequestCallback;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseAdapter;
import com.appublisher.quizbank.common.vip.model.VipExerciseIndexModel;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;
import com.appublisher.quizbank.common.vip.network.VipRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 小班：我的作业
 */
public class VipExerciseIndexActivity extends BaseActivity implements RequestCallback {

    public View statusView;
    public View categoryView;
    public View typeView;
    public TextView statusText;
    public TextView categoryText;
    public TextView typeText;
    public ImageView statusArrow;
    public ImageView categoryArrow;
    public ImageView typeArrow;
    public int status_id = -1;
    public int category_id = -1;
    public int type_id = -1;
    private ListView listView;
    public View emptyView;
    private VipRequest mRequest;
    public List<VipExerciseResp.ExercisesBean> list;
    public VipExerciseAdapter adapter;
    private VipExerciseIndexModel mExerciseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_exercise_index);

        setToolBar(this);
        mRequest = new VipRequest(this, this);
        mExerciseModel = new VipExerciseIndexModel();
        list = new ArrayList<>();
        adapter = new VipExerciseAdapter(this, list);
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
        statusArrow = (ImageView) findViewById(R.id.vip_status_arrow);
        categoryArrow = (ImageView) findViewById(R.id.vip_category_arrow);
        typeArrow = (ImageView) findViewById(R.id.vip_type_arrow);
        listView = (ListView) findViewById(R.id.listView);
        emptyView = findViewById(R.id.empty_view);

        showLoading();
        mRequest.getVipFilter();
    }

    public void setValues() {
        listView.setAdapter(adapter);
        statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseModel.showStatusPop(VipExerciseIndexActivity.this);
            }
        });

        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseModel.showCategoryPop(VipExerciseIndexActivity.this);
            }
        });

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseModel.showTypePop(VipExerciseIndexActivity.this);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mExerciseModel.dealExerciseSkip(VipExerciseIndexActivity.this, position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    public void refreshData() {
        mRequest.getVipExercises(status_id, category_id, type_id);
        showLoading();
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        hideLoading();
        if (response == null) return;
        if ("vip_filter".equals(apiName)) {
            mExerciseModel.dealExerciseFilter(response, this);
        } else if ("vip_exercise".equals(apiName)) {
            mExerciseModel.dealExercises(response, this);
        }
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        hideLoading();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        hideLoading();
    }

    @Override
    protected void onDestroy() {
//        VipExerciseIndexModel.statusPop = null;
//        VipExerciseIndexModel.categoryPop = null;
//        VipExerciseIndexModel.typePop = null;
        super.onDestroy();
    }
}
