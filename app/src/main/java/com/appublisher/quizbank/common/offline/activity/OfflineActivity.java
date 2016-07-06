package com.appublisher.quizbank.common.offline.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.BaseActivity;
import com.appublisher.quizbank.common.offline.model.business.OfflineModel;
import com.appublisher.quizbank.common.offline.netdata.PurchasedCoursesResp;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.utils.GsonManager;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.UmengManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 离线视频页面
 */
public class OfflineActivity extends BaseActivity
        implements RequestCallback, View.OnClickListener {

    /**
     * Views
     **/
    public TextView mTvAll;
    public TextView mTvLocal;
    public TextView mTvNone;
    public View mAllLine;
    public View mLocalLine;
    public ListView mLvAll;
    public ListView mLvLocal;

    /**
     * Data
     **/
    public PurchasedCoursesResp mPurchasedCoursesResp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        // Toolbar（特殊处理：保证颜色渐变一致）
        setToolBar(this);
        setTitle("下载管理");

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

        // 检查版本问题,删除以前的下载文件
        OfflineModel.checkVersion(this);
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
                    GsonManager.getModel(response, PurchasedCoursesResp.class);
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
        int i = v.getId();
        if (i == R.id.offline_all_btn) {
            // 全部
            OfflineModel.pressAllBtn(this);
            OfflineModel.showAllList(this);
            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "All");
            UmengManager.onEvent(this, "Video", map);

        } else if (i == R.id.offline_local_btn) {
            // 已下载
            OfflineModel.pressLocalBtn(this);
            OfflineModel.showLocalList(this);

            // Umeng
            HashMap<String, String> map = new HashMap<>();
            map.put("Action", "Done");
            UmengManager.onEvent(this, "Video", map);
        }
    }
}
