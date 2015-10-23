package com.appublisher.quizbank.model.offline.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.business.CommonModel;
import com.appublisher.quizbank.model.offline.model.business.OfflineModel;
import com.appublisher.quizbank.model.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 离线视频页面
 */
public class OfflineActivity extends AppCompatActivity
        implements RequestCallback, View.OnClickListener{

    /** Views **/
    public TextView mTvAll;
    public TextView mTvLocal;
    public TextView mTvNone;
    public View mAllLine;
    public View mLocalLine;
    public ListView mLvAll;
    public ListView mLvLocal;

    /** Data **/
    public PurchasedCoursesResp mPurchasedCoursesResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        // Toolbar（特殊处理：保证颜色渐变一致）
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.apptheme));
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CommonModel.setBarTitle(this, "下载管理");

        // Init view
        mTvAll = (TextView) findViewById(R.id.offline_all_btn);
        mTvLocal = (TextView) findViewById(R.id.offline_local_btn);
        mAllLine = findViewById(R.id.offline_all_line);
        mLocalLine = findViewById(R.id.offline_local_line);
        mLvAll = (ListView) findViewById(R.id.offline_all_lv);
        mLvLocal = (ListView) findViewById(R.id.offline_local_lv);
        mTvNone = (TextView) findViewById(R.id.offline_none_tv);

        mTvAll.setOnClickListener(this);
        mTvLocal.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLvLocal.getVisibility() == View.VISIBLE) {
            OfflineModel.showLocalList(this);
        } else {
            OfflineModel.showAllList(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response == null || apiName == null) {
            ProgressDialogManager.closeProgressDialog();
            return;
        }

        if ("purchased_courses".equals(apiName)) {
            mPurchasedCoursesResp =
                    GsonManager.getGson().fromJson(response.toString(), PurchasedCoursesResp.class);
            OfflineModel.dealPurchasedCoursesResp(this, mPurchasedCoursesResp);
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.offline_all_btn:
                OfflineModel.pressAllBtn(this);
                OfflineModel.showAllList(this);
                break;

            case R.id.offline_local_btn:
                OfflineModel.pressLocalBtn(this);
                OfflineModel.showLocalList(this);
                break;
        }
    }
}
